package de.uni_due.ltl.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;

public class Id2OutcomeUtil {

	/**
	 * reads a map that stores the id2outcomes
	 * 
	 * @param path
	 * @return
	 * @throws ResourceInitializationException 
	 */
	public static Map<String, Integer> getId2OutcomeMap(String path) throws ResourceInitializationException {
		Map<String, Integer> id2Outcome = new HashMap<String, Integer>();
		List<String> labels=null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#labels")){
					labels=getLabels(line);
				}
				if (!line.startsWith("#")) {
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					int indexOfOne=getIndexOfOne(prediction.split("=")[1]);
					String label =labels.get(indexOfOne);
					int outCome = resolvePolarity(label);
					id2Outcome.put(id, outCome);
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return id2Outcome;
	}
	
	/**
	 * reads a map that stores the id2outcomes
	 * 
	 * @param path
	 * @return
	 * @throws ResourceInitializationException 
	 */
	public static Map<String, String> getId2OutcomeMap_String(String path) throws ResourceInitializationException {
		Map<String, String> id2Outcome = new HashMap<String, String>();
		List<String> labels=null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#labels")){
					labels=getLabels(line);
				}
				if (!line.startsWith("#")) {
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					int indexOfOne=getIndexOfOne(prediction.split("=")[1]);
					String label =labels.get(indexOfOne);
					id2Outcome.put(id, label);
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return id2Outcome;
	}

	public static int getIndexOfOne(String resultVector) throws Exception {
		int i=0;
		for(String dim:resultVector.split(",")){
			if(dim.equals("1")){
				return i;
			}
			i++;
		}
		throw new Exception(resultVector+"does not contain a 1");
	}

	
	/**
     * Retrieves the list of labels from the respective header line of the id2outcome file, sorted
     * according to their index.
     * 
     * @param line
     *            the line of the id2outcome file which contains the labels and their indices
     * @return a list of labels sorted by index, ascending
     * @throws UnsupportedEncodingException
     */
    public static List<String> getLabels(String line)
        throws UnsupportedEncodingException
    {
        String[] numberedClasses = line.split(" ");
        List<String> labels = new ArrayList<String>();

        for (int i = 1; i < numberedClasses.length; i++) {
            String className = numberedClasses[i].split("=")[1];
            labels.add(URLDecoder.decode(className, "UTF-8"));
        }
        return labels;
    }
	
    public static int resolvePolarity(String polarity) throws Exception {
		if (polarity.equals("FAVOR")) {
			return 1;
		} else if (polarity.equals("AGAINST")) {
			return -1;
		} else if (polarity.equals("PRESENT")) {
			return 1;
		}
		else if (polarity.equals("NONE")){
			return 0;
		}
		throw new Exception(polarity+ " not a valid polarity; allowed: PRESENT,FAVOR,AGAINST and NONE");	
	}

    public static Map<String, Boolean> getId2Correct(String path) throws ResourceInitializationException {
		Map<String, Boolean> id2Outcome = new HashMap<String, Boolean>();
		List<String> labels=null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#labels")){
					labels=getLabels(line);
				}
				if (!line.startsWith("#")) {
					String gold = line.split(";")[1];
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					//TODO: hack!
					id=id.split("_")[1];
					int indexOfOnePrediction=getIndexOfOne(prediction.split("=")[1]);
					int indexOfOneGold=getIndexOfOne(gold);
					if(indexOfOnePrediction==indexOfOneGold){
						id2Outcome.put(id, true);
					}else{
						id2Outcome.put(id, false);
					}
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return id2Outcome;
	}

	public static Map<String, Integer> getId2GoldMap(String path) throws ResourceInitializationException {
		Map<String, Integer> id2Outcome = new HashMap<String, Integer>();
		List<String> labels=null;
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("#labels")){
					labels=getLabels(line);
				}
				if (!line.startsWith("#")) {
					String gold = line.split(";")[1];
					String prediction = line.split(";")[0];
					String id = prediction.split("=")[0];
					int indexOfOne=getIndexOfOne(gold);
					String label =labels.get(indexOfOne);
					int outCome = resolvePolarity(label);
					id2Outcome.put(id, outCome);
				}
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		return id2Outcome;
	}
}
