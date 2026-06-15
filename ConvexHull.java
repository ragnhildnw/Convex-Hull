import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;
/**
 * A basic class to allow the precode to compile. You will need to implement the
 * logic for finding what points make up the convex hull.
 * 
 */
public class ConvexHull {
    int n, seed, MAX_X, MAX_Y, MIN_X, MIN_Y, x[], y[];
    IntList innhylling = new IntList();
    

    ConvexHull(final int n, final int seed, final NPunkter17 nPunkter17) {
        this.n = n;
        this.seed = seed;
        this.x = new int[n];
        this.y = new int[n];
        nPunkter17.fyllArrayer(x, y);
        for (int i = 0; i < n; i++) {
            if (x[i] > x[MAX_X])
                MAX_X = i;
            else if (x[i] < x[MIN_X])
                MIN_X = i;
            if (y[i] > y[MAX_Y])
                MAX_Y = i;
        }
    }

    /* A er max og B er min så jeg får linjeretningen "bakover" og dermed negativ avstand på alt "over" */
    public void finnInnhyllingSekv(){
        
        /* punkter med negativ avstand */
        IntList over = new IntList();
        
        /* punkter med positiv avstand */
        IntList under = new IntList();

        int[] punktNegPos = new int[2];
        punkterOverUnder(over, under, punktNegPos);
        int punktMaksNeg = punktNegPos[0]; int punktMaksPos = punktNegPos[1];
        
        innhylling.add(MAX_X);
        /* rekursivt over */
        finnPunkt(MAX_X, punktMaksNeg, over);
        innhylling.add(punktMaksNeg);
        
        finnPunkt(punktMaksNeg, MIN_X, over);
        innhylling.add(MIN_X);
        
        /* rekursivt under */
        finnPunkt(MIN_X, punktMaksPos, under); 
        innhylling.add(punktMaksPos);
        
        finnPunkt(punktMaksPos, MAX_X, under); 
    }
    
    public void finnInnhyllingParallelt(){ 
        
        int[] maksNegPos = new int[2]; /* punkt med: [0] = maks negativ avstand, [1] - maks positiv avstand */
        
        /* over = punkter med negativ avstand, under =  punkter med positiv avstand */ 
        IntList over = new IntList(); IntList under = new IntList();
        
        // finnStartLister(over, under, maksNegPos);
        punkterOverUnder(over, under, maksNegPos);

        ForkJoinPool pool = ForkJoinPool.commonPool();        
        innhylling = pool.invoke(new StartRekursjon(MIN_X, MAX_X, maksNegPos[0], maksNegPos[1], this, over, under));
        pool.close();

    }

    /**
     * Legger punkt-indeksene inn i listen de hører til i, finner høyeste og laveste punkt.  
     * maksNegativPositiv:
     *     - Indeks 0 inneholder indeksen til punktet med mest negativ avstand. 
     *     - Indeks 1 inneholder indeksen til punktet med mest positiv avstand. 
    */
   public void punkterOverUnder(IntList over, IntList under, int[] maksNegativPositiv){
       
       int abc[] = finnabc(MAX_X, MIN_X);
       int maksNegativAvstand = 1, punktMaksNeg = -1;
       int maksPositivAvstand = -1, punktMaksPos = -1;
       
       for (int i = 0; i < x.length; i++){
           if (i == MIN_X || i == MAX_X) continue;
           int midl = avstand(i, abc); 
           
           if (midl <= 0) { 
               over.add(i); 
               if (midl < maksNegativAvstand){
                   maksNegativAvstand = midl;
                   punktMaksNeg = i;
                }
            }
            else{
                under.add(i); 
                if (midl > maksPositivAvstand){
                    maksPositivAvstand = midl;
                    punktMaksPos = i;
                }
            } 
        }
        maksNegativPositiv[0] = punktMaksNeg;
        maksNegativPositiv[1] = punktMaksPos;
    }
    
    /**
     * Leter gjennom listen med relevante punkter etter det som er lengst unna, fortsetter rekursivt. 
     * Legger til nye punkter i globalt skrog. 
     * 
     * a, b = indeks for punktene som danner linjen vi jobber med
     */
    public void finnPunkt(final int a, final int b, final IntList mulige ){ 
        IntList nyePunkter = new IntList();
        
        int punktLengst = finnLengst(a, b, mulige, nyePunkter);
        if(nyePunkter.len == 0 || punktLengst == -1) return; 
        
        /* høyre */
        finnPunkt(a, punktLengst, nyePunkter);
        innhylling.add(punktLengst); 
        
        /* venstre */
        finnPunkt(punktLengst, b, nyePunkter);
    }

    /**
     * Itererer gjennom listen punktene i listen "mulige",
     * legger til alle punkter med negativ avstand  i listen "nyePunkter"
     * returnerer indeksen til punktet lengst unna linjen
    
     */
    public int finnLengst(final int a, final int b, IntList mulige, IntList nyePunkter){

        int[] abc = finnabc(a, b);
    
        int lengst = 1 /* verdi */, punktLengst = -1 /* indeks for punkt */;
        for (int i = 0; i < mulige.len; i++){
            if (mulige.get(i) == a || mulige.get(i) == b) continue; 
            int midl = avstand(mulige.get(i), abc);
            // sjekker om punktet er "over" linjen
            if (midl < 0) {
                nyePunkter.add(mulige.get(i));
            }
            if (midl == 0) {
                // punktet er "på" linjen
                if (mellomPunkter(a, b, mulige.get(i))) {
                    nyePunkter.add(mulige.get(i));
                    if (midl < lengst){
                        lengst = midl;
                        punktLengst = mulige.get(i);
                    }
                }// legger kun til punktet om det ligger mellom punktene våre.
                continue;
            }
            if (midl < lengst){
                lengst = midl;
                punktLengst = mulige.get(i);
            }
        }
        return punktLengst;
    }
    /**
     * Sjeker om punktet som ligger på linjen ligger _mellom_ a og b. 
     */
    public boolean mellomPunkter(final int a, final int b, final int punkt){
        // på x linje
        if (x[a] == x[b]) // hvis avstanden er 0 og a og b har samme x, må også punkt sitt x være likt. 
            if ((y[a] < y[punkt] && y[punkt] < y[b]) || (y[b] < y[punkt] && y[punkt] < y[a])) return true;
        
            // på y linje
        if (y[a] == y[b])
            if ((x[a] < x[punkt] && x[punkt] < x[b]) || (x[b] < x[punkt] && x[punkt] < x[a])) return true;
        
        // på skrå mot høyre
        if ((x[a] < x[punkt] && x[punkt] < x[b]))
            // oppover
            if((y[a] < y[punkt] && y[punkt] < y[b])) return true;
            // nedover
            if((y[b] < y[punkt] && y[punkt] < y[a])) return true;
        
            // på skrå mot venstre
        if ((x[b] < x[punkt] && x[punkt] < x[a]))
            // nedover
            if((y[b] < y[punkt] && y[punkt] < y[a])) return true;
            // oppover
            if((y[a] < y[punkt] && y[punkt] < y[b])) return true;

        return false; 
    }

    /** 
     * Brukes til å regne ut "linjen" og relativ avstand. 
     */
    public int[] finnabc(int en, int to){
        int abc[] = new int[]{
            /* a */ y[en] - y[to],
            /* b */ x[to] - x[en],
            /* c */ y[to] * x[en] - y[en] * x[to]
        };
        return abc;
    }
    
    public int avstand(int punktIndeks, int abc[]){
        return 
            /* a */ abc[0] * x[punktIndeks] + 
            /* b */ abc[1] * y[punktIndeks] + 
            /* c */ abc[2];
    }

    /** 
     * Printer alle koordinatene
     */
    public void printAllePunkter(){
        for (int i = 0; i < x.length; i++)
            System.out.print("(" + x[i] + "," + y[i] + ")\t");
        System.out.println();
    }

    /**
     * Printer koordinatene til alle punktene i listen
     */
    public void printPunkter(IntList punkter){
        for (int i = 0; i < punkter.len; i++)
            System.out.print("(" + x[punkter.get(i)] + "," + y[punkter.get(i)] + ")\t");
        System.out.println();
    }

    public void testUnik(String modus){
        HashSet<Integer> set = new HashSet<>();
        int setSize = 0; 
        for (int i = 0; i < innhylling.len; i++){
            set.add(innhylling.get(i));
            if (++setSize != set.size()){
                System.out.println("Fant duplikat: " + innhylling.get(i));
                setSize--;
            }
        }
        assert set.size() == innhylling.len : "Ulik lengde etter " + modus + " kjøring - listen inneholder duplikater";
    }
    
    public static void main(String[] args) {
        final int n = Integer.parseInt(args[0]);
        final int seed = Integer.parseInt(args[1]);

        ConvexHull ch = new ConvexHull(n, seed, new NPunkter17(n, seed));
        
        ch.finnInnhyllingSekv();

        ch.testUnik("sekvensiell");
        System.out.println("testet sekvensiell");
        IntList coHull = ch.innhylling;
        Oblig4Precode precode = new Oblig4Precode(ch, coHull);
        precode.drawGraph();
        
        ConvexHull chPar = new ConvexHull(n, seed, new NPunkter17(n, seed));
        chPar.finnInnhyllingParallelt();
        
        chPar.testUnik("parallel");
        System.out.println("testet paralelll");
        coHull = chPar.innhylling;
        precode = new Oblig4Precode(chPar, coHull);
        precode.drawGraph();
    }

}
