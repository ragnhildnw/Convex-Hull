import java.util.concurrent.RecursiveTask;

class FinnPunkt extends RecursiveTask<IntList>{
    ConvexHull ch;
    int a, b;
    IntList mulige, innhylling;

    FinnPunkt(ConvexHull c, int a, int b, IntList m){
        ch = c; 
        this.a = a; this.b = b; 
        mulige = m; //innhylling = ih;
    }

    /** 
     * Skal gjøre den rekursive delen av algoritmen, steg for steg, dele opp i flere kall og flere RecurisveTask objekter. 
     */
    public IntList compute(){

        // if(mulige.len < 10_000) return finnPunktSekv(a, b, mulige); // gir ikke tydelig utslag på tid

        IntList nyePunkter = new IntList(); 
        
        int punktLengst = ch.finnLengst(a, b, mulige, nyePunkter); 
        if(nyePunkter.len == 0 || punktLengst == -1) return new IntList(); 
        
        /* venstre */ 
        FinnPunkt venstre = new FinnPunkt(ch, punktLengst, b, nyePunkter); 
        venstre.fork();
        /* høyre */
        FinnPunkt høyre = new FinnPunkt(ch, a, punktLengst, nyePunkter);
        IntList høyreIL = høyre.compute(); høyreIL.add(punktLengst);
        
        høyreIL.append(venstre.join());
        return høyreIL;
    }

    public IntList finnPunktSekv(final int a, final int b, final IntList mulige ){ 

        IntList nyePunkter = new IntList();
        
        int punktLengst = ch.finnLengst(a, b, mulige, nyePunkter);
        if(nyePunkter.len == 0 || punktLengst == -1) return new IntList(); 
        
        IntList innhylling = new IntList();
        /* høyre */
        innhylling.append(finnPunktSekv(a, punktLengst, nyePunkter));
        innhylling.add(punktLengst); 
        
        /* venstre */
        innhylling.append(finnPunktSekv(punktLengst, b, nyePunkter));
        return innhylling;
    }
    
}
