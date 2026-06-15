import java.util.concurrent.RecursiveTask;

class StartRekursjon extends RecursiveTask<IntList>{
    int minX, maksX, maksNeg, maksPos;
    ConvexHull ch; 
    IntList over, under;

    StartRekursjon(int min, int maks, int neg, int pos, ConvexHull h, IntList o, IntList u){
        minX = min; maksX = maks; maksNeg = neg; maksPos = pos;
        ch = h; 
        over = o; under = u;
    }

    public IntList compute(){

        IntList innhylling = new IntList(); innhylling.add(maksX);

        FinnPunkt en = new FinnPunkt(ch, maksX, maksNeg, over);
        en.fork();
        
        FinnPunkt to = new FinnPunkt(ch, maksNeg, minX, over);
        to.fork();

        FinnPunkt tre = new FinnPunkt(ch, minX, maksPos, under);
        tre.fork();
       
        FinnPunkt fire = new FinnPunkt(ch, maksPos, maksX, under);
        fire.fork(); 
        
        innhylling.append(en.join()); innhylling.add(maksNeg); innhylling.append(to.join());
        innhylling.add(minX); innhylling.append(tre.join()); innhylling.add(maksPos); innhylling.append(fire.join());
        
        return innhylling;
    }
}