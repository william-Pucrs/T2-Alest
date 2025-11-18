import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

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
        public int avl;
        public int nivel;

        public Nodo(int v) 
        {
            pai=filhosDaEsquerda=filhosDaDireita=null;            
            this.valor=v;
            avl = 0;
            nivel = 0;
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
        else
        {
            Nodo pai = findFather(raiz, e);
            aux.pai=pai;
            // 10 >= 17 
            if(aux.valor<=pai.valor)
                pai.filhosDaEsquerda=aux;
            else
                pai.filhosDaDireita=aux;
        }
        aux.nivel = (aux != raiz) ? aux.pai.nivel+1 : 0;

        updateAVL(aux);
        checkAVLOnBranch(aux);

        nNodos++;
    }

    public boolean removeBranch(Integer e)
    {
        Nodo removedNode = findNode(raiz, e);

        if (removedNode == null) return false;

        Nodo father = removedNode.pai;
        
        int numOfRemovedNodes = positionsPre(removedNode).length;

        if (father.filhosDaDireita == removedNode)
            father.filhosDaDireita = null;
        else
            father.filhosDaEsquerda = null;

        removedNode.pai = null;

        updateAVL(father);
        checkAVLOnBranch(father);

        nNodos -= numOfRemovedNodes;
        return true;
    }

    private void updateAVL(Nodo ref)
    {
        while (ref != null)
        {
            ref.avl = -branchHeight(ref.filhosDaEsquerda) + branchHeight(ref.filhosDaDireita);
            ref = ref.pai;
        }
    }

    private boolean checkAVLOnBranch(Nodo ref)
    {
        boolean hadTransformation = false;

        while (ref != null) 
        {
            if (Math.abs(ref.avl) > 1)
            {
                hadTransformation = true;

                if (ref.avl > 0)
                {
                    if (ref.filhosDaDireita.avl < 0)
                        rightRotation(ref);
                    ref = leftRotation(ref);
                }
                else
                {
                    if (ref.filhosDaEsquerda.avl > 0)
                        leftRotation(ref.filhosDaEsquerda); 
                    ref = rightRotation(ref);
                }
            }
            ref = ref.pai;
        }

        return hadTransformation;    
    }

    private boolean checkAVLAllTree()
    {
        boolean hadTransformation = false;

        Nodo leafNodes[] = allLeafNodes();

        for(Nodo node : leafNodes)
        {
            while (node != null) 
            {
                if (Math.abs(node.avl) > 1)
                {
                    hadTransformation = true;

                    if (node.avl > 0)
                    {
                        if (node.filhosDaDireita.avl < 0)
                            rightRotation(node);
                        node = leftRotation(node);
                    }
                    else
                    {
                        if (node.filhosDaEsquerda.avl > 0)
                            leftRotation(node.filhosDaEsquerda); 
                        node = rightRotation(node);
                    }
                }
                node = node.pai;
            }
        }

        return hadTransformation;
    }

    private Nodo leftRotation(Nodo ref)
    {
        Nodo branchRoot = ref.pai;
        
        Nodo substitute = ref.filhosDaDireita;
        Nodo T2 = substitute.filhosDaEsquerda;

        if (branchRoot != null)
        {
            if (branchRoot.filhosDaDireita == ref)
                branchRoot.filhosDaDireita = substitute;
            else
                branchRoot.filhosDaEsquerda = substitute;
        }

        ref.filhosDaDireita = T2;
        substitute.filhosDaEsquerda = ref;
        substitute.pai = branchRoot;
        ref.pai = substitute;
        
        if (T2 != null)
            T2.pai = ref;
                                    
        updateAVL(substitute);
        return substitute;
    }

    private Nodo rightRotation(Nodo ref)
    {
        Nodo branchRoot = ref.pai;
        Nodo substitute = ref.filhosDaEsquerda;
        Nodo T2 = substitute.filhosDaDireita;

        if (branchRoot != null)
        {
            if (branchRoot.filhosDaDireita == ref)
                branchRoot.filhosDaDireita = substitute;
            else
                branchRoot.filhosDaEsquerda = substitute;
        }

        substitute.pai = branchRoot;
        ref.pai = substitute;
        ref.filhosDaEsquerda = T2;
        substitute.filhosDaDireita = ref;

        if (T2 != null)
            T2.pai = ref;
        
        updateAVL(substitute);
        return substitute;
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
        return !isExternal(e);
    }

    public boolean isExternal(Integer e)
    {
        Nodo node = findNode(raiz, nNodos);
    
        if (node == null)
            throw new IllegalArgumentException("Não foi possivel encontrar o nodo passado");

        return (node.filhosDaEsquerda == null && node.filhosDaDireita == null);
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

    public int level(Nodo e)
    {
        if(e==null) 
            return -1;

        int nivel=0;

        while(e.pai!=null)
        {
            nivel++;
            e= e.pai;
        }

        return nivel;
        
    }

    //Node height
    private int navegaPelosNodos1(Nodo ref, int altura)
    {

        if(ref!=null){

            // se for um nodo folha entao calcula o nivel
            if((ref.filhosDaEsquerda==null)&&(ref.filhosDaDireita==null))
            {
                int nvl=0;
                Nodo aux=ref;
                while(aux.pai!=null)
                {
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
    //Node height
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
        // navega em largura
        // alternativas 1 e 2
        //return navegaPelosNodos2(raiz, -1);

        Integer[] largura = positionsWidth();
        int altura = level(largura[largura.length-1]);
        return altura;
    }

    public int branchHeight(Nodo ref)
    {
        return navegaPelosNodos2(ref, -1);
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
    
    private boolean isLeaf(Nodo ref)
    {
        if (ref == null) return false;
        return (ref.filhosDaEsquerda == null && ref.filhosDaDireita == null); 
    }

    private Nodo[] allLeafNodes()
    {
        Nodo list[] = positionsWidth(true);
        
        Stack<Nodo> leafStack = new Stack<>();
        
        for (Nodo obj : list)
        {
            if (isLeaf(obj))
                leafStack.push(obj);
        }
        
        return leafStack.toArray(new Nodo[0]);
    }

    
    public Integer[] positionsPre()
    {
        if(nNodos==0) return null;

        Integer[] resultado = new Integer[nNodos];

        //preordem(raiz, resultado, 0);
        caminhamentoEmProfundidade(raiz, resultado, 0, profundidade.Pre);

        return resultado;
    }
    
    public Integer[] positionsPre(Nodo ref)
    {
        if(nNodos==0) return null;

        Integer[] resultado = new Integer[nNodos];

        //preordem(raiz, resultado, 0);
        caminhamentoEmProfundidade(ref, resultado, 0, profundidade.Pre);

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
 
 //
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

    public Nodo[] positionsWidth(boolean returnAsNode)
    {
        if(nNodos==0) return null;
        Nodo[] resultado;
        LinkedList<Nodo> fila;
        resultado = new Nodo[nNodos];
        int idx=0;

        fila = new LinkedList<Nodo>();
        fila.add(raiz);

        do
        {
            Nodo aux = fila.remove();

            if(aux.filhosDaEsquerda!=null) fila.add(aux.filhosDaEsquerda);
            if(aux.filhosDaDireita!=null)  fila.add(aux.filhosDaDireita);

            resultado[idx]=aux;
            idx++;

        }
        while(!fila.isEmpty());

        return resultado;

    }



    public static void main(String[] args) 
    {
        System.out.println("\n===== TESTE 1: Inserções Simples =====");
        ArvoreBinariaPesquisa avl = new ArvoreBinariaPesquisa();

        int[] valores1 = {40,20,10,30,60,70,50,35,33,37};
        for (int v : valores1)
            avl.add(v);

        imprimeTudo(avl);


        System.out.println("\n===== TESTE 2: Caso LL =====");
        avl = new ArvoreBinariaPesquisa();
        int[] LL = {30,20,10};
        for (int v : LL) avl.add(v);
        imprimeTudo(avl);


        System.out.println("\n===== TESTE 3: Caso RR =====");
        avl = new ArvoreBinariaPesquisa();
        int[] RR = {10,20,30};
        for (int v : RR) avl.add(v);
        imprimeTudo(avl);


        System.out.println("\n===== TESTE 4: Caso LR =====");
        avl = new ArvoreBinariaPesquisa();
        int[] LR = {30,10,20};
        for (int v : LR) avl.add(v);
        imprimeTudo(avl);


        System.out.println("\n===== TESTE 5: Caso RL =====");
        avl = new ArvoreBinariaPesquisa();
        int[] RL = {10,30,20};
        for (int v : RL) avl.add(v);
        imprimeTudo(avl);


        System.out.println("\n===== TESTE 6: Remoção de subárvore =====");
        avl = new ArvoreBinariaPesquisa();
        int[] valores2 = {50,30,70,20,40,60,80,10,25};
        for (int v : valores2) avl.add(v);

        imprimeTudo(avl);

        System.out.println("\n--- Removendo subárvore de 30 ---");
        avl.removeBranch(30);
        imprimeTudo(avl);

        System.out.println("\n--- Removendo subárvore de 70 ---");
        avl.removeBranch(70);
        imprimeTudo(avl);


        System.out.println("\n===== TESTE 7: 20 inserções aleatórias =====");
        avl = new ArvoreBinariaPesquisa();
        java.util.Random r = new java.util.Random();

        for (int i = 0; i < 20; i++)
            avl.add(r.nextInt(100));

        imprimeTudo(avl);

        System.out.println("\nTeste finalizado.");
    }

    private static void imprimeTudo(ArvoreBinariaPesquisa a)
    {
        System.out.print("Pre ordem : ");
        printArray(a.positionsPre());

        System.out.print("Pos ordem : ");
        printArray(a.positionsPos());

        System.out.print("Central   : ");
        printArray(a.positionsCentral());

        System.out.print("Largura   : ");
        printArray(a.positionsWidth());

        System.out.println("Altura    : " + a.height());
        System.out.println("Tamanho   : " + a.size());
        System.out.println("Raiz      : " + a.getRoot());
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