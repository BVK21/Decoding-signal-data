import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignalProcessor {

    public static double threshold ;


    public static void main(String[] args) throws Exception {
        List<List<Double>> partitionedList = readSignalFromFile("resources/proj1_testsignal1");
        System.out.println(getCleanedBitsString(getBitsString(partitionedList,threshold)));
    }


    public static List<List<Double>> readSignalFromFile(String filePath) throws Exception {
        if (filePath == null || filePath.isEmpty())
            throw new Exception("Invalid Input");
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String str;

        List<Double> list = new ArrayList<>();
        while ((str = in.readLine()) != null) {
            list.add(Double.parseDouble(str));
        }
        List<Double> sampleData = new ArrayList<>();
        for(int i = 0 ; i < 1000000 ; i ++){
            sampleData.add(list.get(i));
        }
        threshold = getThreshold(sampleData);
        int startIndex = 0 ;
        for(int i = 0 ; i < list.size() ; i++){
            if(list.get(i) > threshold){
                startIndex = i;
                break;
            }
        }
        startIndex = startIndex - 5;
        list = list.subList(startIndex, list.size());
        List<List<Double>> partitionedList = Lists.partition(list, 100);
        return partitionedList;
    }

    public static double getThreshold(List<Double> signalData) throws Exception {
        if(signalData == null || signalData.size() != 1000000)
            throw new Exception("Invalid Parameter Passed !!");
        double sum = 0 ;
        double mean = 0 ;
        double standardDeviation  = 0 ;
        for(Double data : signalData){
            sum+=data;
        }
        mean = sum/signalData.size();

        for(Double data: signalData) {
            standardDeviation += Math.pow(data - mean, 2);
        }
        standardDeviation = Math.sqrt(standardDeviation/signalData.size());
        return (mean * 8 + standardDeviation * 16);

    }

    public static String getBitsString(List<List<Double>> partitionedSignalData , double threshold) throws Exception {
        try {
            if (partitionedSignalData == null || partitionedSignalData.size() == 0)
                throw new Exception("Invalid Parameter");
            StringBuilder sb = new StringBuilder();

            for (List<Double> data : partitionedSignalData) {
                if (data.size() == 100) {
                    boolean bitFound = false;
                    for (int i = 0; i < 20; i++) {
                        if (data.get(i) > threshold) {
                            sb.append("0");
                            bitFound = true;
                            break;
                        }
                    }
                    if (!bitFound) {
                        for (int i = 20; i < 100; i++) {
                            if (data.get(i) > threshold) {
                                sb.append("1");
                                bitFound = true;
                                break;
                            }
                        }
                    }
                }
            }
            sb.delete(0,8);
            return sb.toString();
        }catch (Exception e){
            throw new Exception("Exception");
        }
    }

    public static StringBuilder getCleanedBitsString(String uncleanedBits) throws Exception {
        try {

            if (uncleanedBits == null || uncleanedBits.isEmpty()) {
                throw new Exception("Invalid Parameter ");
            }
            StringBuilder sb = new StringBuilder();
            Iterable<String> chunks = Splitter.fixedLength(7).split(uncleanedBits);
            for (String s : chunks) {
                System.out.println("The Binary String :" + s);
                if(s.length() > 4) {
                    sb.append(s, 0, 4);
                }
            }
            System.out.println(new String(binaryToBytes(sb.toString()),StandardCharsets.UTF_8));
            return sb;
        }catch (Exception e){
            throw new Exception("Exception");
        }
    }

    public static String binaryToText(String binary) {
        return Arrays.stream(binary.split("(?<=\\G.{8})"))/* regex to split the bits array by 8*/
                .parallel()
                .map(eightBits -> (char)Integer.parseInt(eightBits, 2))
                .collect(
                        StringBuilder::new,
                        StringBuilder::append,
                        StringBuilder::append
                ).toString();
    }

    public static byte[] binaryToBytes(String input) {
        byte[] ret = new byte[input.length() / 8];
        for (int i = 0; i < ret.length; i++) {
            String chunk = input.substring(i * 8, i * 8 + 8);
            ret[i] = (byte) Short.parseShort(chunk, 2);
        }
        return ret;
    }
}

