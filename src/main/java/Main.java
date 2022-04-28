
import com.google.gson.Gson;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {


    static Queue<Data> dataQueue = new LinkedList<Data>();

    static String device_kid = "device1";
    static String key_set = "keys/keyset";





    public static void main(String[] args) throws JOSEException, IOException, ParseException {

        try{
            JWKSet localKeys = JWKSet.load(new File(key_set));
            JWK foundKey = localKeys.getKeyByKeyId(device_kid);
            if (foundKey != null) {
                String message = "key id already exists locally";
                ECKey key = foundKey.toECKey();
                System.out.println("BEGIN PUBLIC KEY");
                System.out.println("kid="+key.getKeyID());
                System.out.println("x="+key.getX().toString());
                System.out.println("y="+key.getY().toString());
                System.out.println("crv="+key.getCurve().toString());
                System.out.println("alg="+key.getAlgorithm().toString());
                System.out.println("use="+key.getKeyUse().toString());
                System.out.println("END PUBLIC KEY");
            }else{
                JWKGenerator jwkGenerator = new JWKGenerator();
                ECKey key = jwkGenerator.generateECJWK(device_kid, "P-256", "sig", "ES256");
                JWKGenerator.writeKeyToFile(true, key_set, key, new Gson());
            }
        }catch (IOException | ParseException ignored){
        }
//        readAndEnqueeData();
//        sendDataFromQueue();
    }

    private static void readAndEnqueeData() {
        try (CSVReader reader = new CSVReader(new FileReader("data/result.csv"))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                Data data = new Data(lineInArray[0],lineInArray[1], lineInArray[2], lineInArray[3]);
                dataQueue.add(data);
            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }


    static Runnable helloRunnable = new Runnable() {
        public void run() {
            Data data = dataQueue.poll();
            System.out.println("\n----------");
            System.out.println("Dado enviado:");
            System.out.println("Timestamp: "+data.getTimestamp());
            System.out.println("Corrente: "+data.getCurrent());
            System.out.println("Energia: "+data.getEnergy());
            System.out.println("Potencia: "+data.getPower());
            System.out.println("Faltam "+dataQueue.size()+" elementos na fila para serem enviados");
            System.out.println("----------");
        }
    };

    static public void sendDataFromQueue(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);
    }


}
