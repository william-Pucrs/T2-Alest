import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.management.AttributeNotFoundException;

public class ArvoreBinariaPesquisa 
{
    private Nodo raiz;
    private int nNodos;

    private enum profundidade {Pos, Pre, Central};

    private class Nodo 
    {
        public Nodo pai;
        public int valor;
        public Nodo filhosDaEsquerda;
        public Nodo filhosDaDireita;

        public Nodo(int v) 
        {
            pai=filhosDaEsquerda=filhosDaDireita=null;            
            this.valor=v;
        }
    }

    public ArvoreBinariaPesquisa() 
    {
        raiz=null;
        nNodos=0;
    }

    public void add(Integer e)
    {
        Nodo aux = new Nodo(e);
        if(nNodos==0)
            raiz=aux;
        else{
            Nodo pai = findFather(raiz, e);
            aux.pai=pai;
            // 10 >= 17 
            if(aux.valor<=pai.valor)
                pai.filhosDaEsquerda=aux;
            else
                pai.filhosDaDireita=aux;
        }
        nNodos++;
    }

    private Nodo findFather(Nodo ref, Integer e)
    {

        if(e<=ref.valor){
            // seguir a esquerda
            if(ref.filhosDaEsquerda!=null)
                return findFather(ref.filhosDaEsquerda, e);
            else // filho da esquerda é nulo
                return ref;
        }            
        else
        { 
            // seguir a direita
            if(ref.filhosDaDireita!=null)
                return findFather(ref.filhosDaDireita, e);
            else // filho da direita é nulo
                return ref;
        }

    }

    public boolean isInternal(Integer e)
    {
        return false;
    }

    public boolean isExternal(Integer e)
    {
        return false;
    }
    
    public boolean contains(Integer e)
    {
        return (findNode(raiz, e) != null);
    }

    public Integer getLeft(Integer e)
    {
        Nodo node = findNode(raiz, nNodos);
    
        if (node == null)
            throw new IllegalArgumentException("Não foi possivel encontrar o nodo passado");
        
        Nodo son = node.filhosDaEsquerda;
        return (son != null) ? son.valor : null;
    }

    public Integer getRight(Integer e)
    {
        Nodo node = findNode(raiz, nNodos);
    
        if (node == null)
            throw new IllegalArgumentException("Não foi possivel encontrar o nodo passado");
    
        Nodo son = node.filhosDaDireita;
        return (son != null) ? son.valor : null;
    }

    public boolean hasRight(Integer element)
    {
        return (findNode(raiz, element).filhosDaDireita != null);
    }
    
    public boolean hasLeft(Integer element)
    {
        return (findNode(raiz, element).filhosDaEsquerda != null);
    }
    
    public Integer getParent(Integer e)
    {
        Nodo father = findNode(raiz, nNodos).pai;
        return (father != null) ? father.valor : null;
    }    
    
    // Nao precisa ser implementado

    private Nodo findNode(Nodo ref, int e)
    {
        if(ref!=null)
        {
            if(ref.valor==e) 
                return ref;

            if(e<=ref.valor) 
                return findNode(ref.filhosDaEsquerda, e);
            else    
                return findNode(ref.filhosDaDireita, e);
        }
        return null;
    }

    public int level(Integer e)
    {
        if(e==null) 
            return -1;

        Nodo aux = findNode(raiz, e);
        if(aux==null) 
            return -1;

        int nivel=0;

        while(aux.pai!=null)
        {
            nivel++;
            aux=aux.pai;
        }

        return nivel;
        
    }

    private int navegaPelosNodos1(Nodo ref, int altura)
    {

        if(ref!=null){

            // se for um nodo folha entao calcula o nivel
            if((ref.filhosDaEsquerda==null)&&(ref.filhosDaDireita==null)){
                int nvl=0;
                Nodo aux=ref;
                while(aux.pai!=null){
                    nvl++;
                    aux=aux.pai;
                }
                // se o nivel atual for maior do altura, entao assume a nova altura
                if(nvl>altura) return nvl;
            }
            // senao navega para os filhos
            else{
                int nvlfilho=navegaPelosNodos1(ref.filhosDaEsquerda, altura);
                nvlfilho=navegaPelosNodos1(ref.filhosDaDireita, nvlfilho);
                return nvlfilho;
            }
        }
        return altura;
    }
    private int navegaPelosNodos2(Nodo ref, int altura)
    {

        if(ref!=null){
            altura++;

            // se for um nodo folha entao calcula o nivel
            if((ref.filhosDaEsquerda==null)&&(ref.filhosDaDireita==null))
                return altura;
            // senao navega para os filhos
            else{
                int alturaSubarvoreE=navegaPelosNodos2(ref.filhosDaEsquerda, altura);
                int alturaSubarvoreD=navegaPelosNodos2(ref.filhosDaDireita,  altura);

                //return (alturaSubarvoreE>alturaSubarvoreD)?alturaSubarvoreE:alturaSubarvoreD;
                if(alturaSubarvoreE>alturaSubarvoreD)
                    return alturaSubarvoreE;
                else
                    return alturaSubarvoreD;
            }
        }
        return altura;
    }

    public int height()
    {
        // navega em pre ordem
        // alternativas 1 e 2
        //return navegaPelosNodos2(raiz, -1);

        Integer[] largura = positionsWidth();
        int altura = level(largura[largura.length-1]);
        return altura;


    }

    public boolean removeBranch(Integer e)
    {
        return false;
    }

    public boolean isEmpty()
    {
        return (raiz==null);
        //return (nNodos==0);
    }
    
    public void clear()
    {
        raiz=null;
        nNodos=0;
    }
    
    public int size()
    {
        return nNodos;
    }
    
    public Integer getRoot()
    {
        if(raiz!=null) return raiz.valor;
        else return null;
    }
    
    
    public Integer[] positionsPre()
    {
        if(nNodos==0) return null;

        Integer[] resultado = new Integer[nNodos];

        //preordem(raiz, resultado, 0);
        caminhamentoEmProfundidade(raiz, resultado, 0, profundidade.Pre);

        return resultado;
    }
    
    public Integer[] positionsCentral()
    {
        if(nNodos==0) return null;

        Integer[] resultado = new Integer[nNodos];

        //central(raiz, resultado, 0);
        caminhamentoEmProfundidade(raiz, resultado, 0, profundidade.Central);

        return resultado;
    }

    public Integer[] positionsPos()
    {
        if(nNodos==0) return null;

        Integer[] resultado = new Integer[nNodos];

        //posordem(raiz, resultado, 0);
        caminhamentoEmProfundidade(raiz, resultado, 0, profundidade.Pos);

        return resultado;
    }
 
    private int caminhamentoEmProfundidade(Nodo ref, Integer[] lst, int idx, profundidade tipo)
    {

        if(ref!=null){

            if(tipo==profundidade.Pre)
            {
                // visito o nodo atual
                lst[idx]=ref.valor;
                idx++;
            }

            // visito o filho a esquerda
            idx=caminhamentoEmProfundidade(ref.filhosDaEsquerda, lst, idx, tipo);

            if(tipo==profundidade.Central)
            {
                // visito o nodo atual
                lst[idx]=ref.valor;
                idx++;
            }

            // visito o filho a direita
            idx=caminhamentoEmProfundidade(ref.filhosDaDireita, lst, idx, tipo);

            if(tipo==profundidade.Pos)
            {
                // visito o nodo atual
                lst[idx]=ref.valor;
                idx++;
            }
        }
        return idx;

    }
    
    public Integer[] positionsWidth()
    {
        if(nNodos==0) return null;
        Integer[] resultado;
        LinkedList<Nodo>    fila;
        resultado = new Integer[nNodos];
        int idx=0;

        fila = new LinkedList<Nodo>();
        fila.add(raiz);

        do
        {
            Nodo aux = fila.remove();

            if(aux.filhosDaEsquerda!=null) fila.add(aux.filhosDaEsquerda);
            if(aux.filhosDaDireita!=null)  fila.add(aux.filhosDaDireita);

            resultado[idx]=aux.valor;
            idx++;

        }
        while(! fila.isEmpty());

        return resultado;

    }

    public static void main(String[] args) 
    {
        ArvoreBinariaPesquisa abp = new ArvoreBinariaPesquisa();


        abp.add(40);
        abp.add(20);
        abp.add(10);
        abp.add(30);
        abp.add(60);
        abp.add(70);
        abp.add(50);
        abp.add(35);
        abp.add(33);
        abp.add(37);

        System.out.print("Pre ordem: ");
        printArray( abp.positionsPre());
        System.out.print("Pos ordem: ");
        printArray( abp.positionsPos());
        System.out.print("Central  : ");
        printArray( abp.positionsCentral());
        System.out.print("Largura  : ");
        printArray( abp.positionsWidth());

        System.out.println("Nivel do nodo 35 = "+abp.level(35));
        System.out.println("A altura da arvore = "+abp.height());
    }

    private static void printArray(Integer[] array)
    {
        if (array==null) 
            System.out.println("array vazio");
        else
        {
            System.out.print("[");
            for(int i=0; i<array.length-1; i++)
                System.out.print(array[i]+", ");
            System.out.println(array[array.length-1]+"]");
        }
    }
    
}