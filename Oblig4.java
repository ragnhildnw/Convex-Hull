import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;

class Oblig4{

    private static int[] str = { 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000 };
    private static int antRunder = 7, medianIndeks = 3;
    private int seed;
    private HashMap<Integer, Double> medianTiderSekv = new HashMap<>(), medianTiderPar = new HashMap<>();
    private HashMap<Integer, ArrayList<Double>> tiderSekv = new HashMap<>(), tiderPar = new HashMap<>();

    Oblig4(int s) {seed = s;}

    public ConvexHull kjørSekvensiell(int n, int i){ 

        ConvexHull ch = new ConvexHull(n, seed + i, new NPunkter17(n, seed + i));
        Long t1 = System.nanoTime();
        ch.finnInnhyllingSekv();
        Double totalTid = (System.nanoTime() - t1)/1000.0;
        if (tiderSekv.get(n) == null) tiderSekv.put(n, new ArrayList<>());
        tiderSekv.get(n).add(totalTid);
        ch.testUnik("sekvensiell");
        tiderSekv.get(n).sort(Comparator.naturalOrder());

        return ch;
    }

    public void tegn(ConvexHull ch){
        Oblig4Precode precode = new Oblig4Precode(ch, ch.innhylling);
        precode.drawGraph();
    }

    public ConvexHull kjørParallelt(int n, int i){
        ConvexHull ch = new ConvexHull(n, seed + i, new NPunkter17(n, seed + i));

        Long t1 = System.nanoTime();
        ch.finnInnhyllingParallelt(); 
        Double totalTid = (System.nanoTime() - t1)/1000.0;
        if (tiderPar.get(n) == null) tiderPar.put(n, new ArrayList<>());
        tiderPar.get(n).add(totalTid);
        ch.testUnik("parallell");
        tiderPar.get(n).sort(Comparator.naturalOrder());

        return ch;
    }

    
    public void sammenlignOutput(IntList en, IntList to){
        assert en.len == to.len : "De to listene har ulike lengde. en.len: " + en.len + ", to.len: " + to.len;
        for (int i = 0; i < en.len; i++){
            assert en.get(i) == to.get(i) : "Ulik verdi på indeks " + i + ". en.get(i): " + en.get(i) + ", to.get(i): " + to.get(i);
        }
    }
    
    public void prekodeSkrivTilFil(ConvexHull h){
        
        Oblig4Precode prekode = new Oblig4Precode(h, h.innhylling);
        prekode.writeHullPoints();
        
    }

    /** 
     * tilpassed fra prekoden 
     * Skriver punkt-indekser til fil på formen 
     *      n,modus,tid
     */
    public void skrivTilFil(ConvexHull h, String modus, Double tid){
		String filnavn = "ConvexHullPoints_seed=" + seed + ".txt";

		try (PrintWriter writer = new PrintWriter(new FileWriter(filnavn, true))) {
			writer.printf(
					h.n + "," + modus + "," + tid + "\n"
            );

			if (h.n < 10000) {
                for (int i = 0; i < h.innhylling.size(); i++) {
                    writer.print(h.innhylling.get(i) + " ");
			    }
                writer.println();
            }

			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.printf("Got exception when trying to write file %s : ", filnavn, e.getMessage());
		}

    }
    
    public static void usage(){
        System.out.println("\nUsage:\n\tjava -ea Oblig4 <seed>\n\nWhere seed is an integer.\n");
    }

    public void kjør(){
        ConvexHull chSekv = null;
        ConvexHull chPar = null;

        for (int n: str){
            System.out.println("n: " + n + "\t");

            for (int i = 0; i < antRunder;i++){
                chSekv = kjørSekvensiell(n, i);
                chPar = kjørParallelt(n, i);
                
                System.out.print(i+1 + "\t");
                sammenlignOutput(chSekv.innhylling,chPar.innhylling); 
            }
            tiderSekv.get(n).sort(Comparator.naturalOrder());
            medianTiderSekv.put(n, tiderSekv.get(n).get(medianIndeks));

            tiderPar.get(n).sort(Comparator.naturalOrder());
            medianTiderPar.put(n, tiderPar.get(n).get(medianIndeks));
            
            System.out.println("speedup: " + medianTiderSekv.get(n)/medianTiderPar.get(n));

            if (n < 10000) prekodeSkrivTilFil(chSekv);
            if (chSekv != null && n <= 10_000) tegn(chPar);
            skrivTilFil(chSekv, "sekvensiell",medianTiderSekv.get(n));
            skrivTilFil(chSekv, "parallell",medianTiderPar.get(n));

        }
    }
    public static void main(String[] args){
        int seed;
        try{ 
            seed = Integer.parseInt(args[0]);}
        catch(ArrayIndexOutOfBoundsException e){ usage(); return;}
        
        Oblig4 oblig = new Oblig4(seed);
        oblig.kjør();
        

    }
}