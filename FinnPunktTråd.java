class FinnPunktTråd implements Runnable{
    int[] x, y;
    IntList originalMulige, innhylling;
    int startA, startB;

    FinnPunktTråd(int[] x, int[] y, IntList mulige, IntList i, int a, int b){
        this.x = x; this.y = y; originalMulige = mulige;
        startA = a; startB = b; 
        innhylling = i;
    }

    @Override
    public void run(){
        finnPunkt(startA, startB, originalMulige);
    }

    /**
     * Leter gjennom listen med relevante punkter etter det som er lengst unna, fortsetter rekursivt. 
     * Legger til nye punkter i globalt skrog. 
     * 
     * a, b = indeks for punktene som danner linjen vi jobber med
     */
    public void finnPunkt(final int a, final int b, final IntList mulige ){ 
        // System.out.println("finnPunkt(" + a + ", " + b + ", mulige.len=" + mulige.len);
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
            // punktet er "på" linjen
            if (midl == 0) {

                if (mellomPunkter(a, b, mulige.get(i))) {
                    nyePunkter.add(mulige.get(i));

                    if (midl < lengst){
                        lengst = midl;
                        punktLengst = mulige.get(i);
                    }
                } // legger kun til punktet om det ligger mellom punktene våre.
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

}
