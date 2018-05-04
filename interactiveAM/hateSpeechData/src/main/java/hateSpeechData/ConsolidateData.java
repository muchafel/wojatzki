package hateSpeechData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;

public class ConsolidateData {
	
	public Map<String,Integer> varToIndex=new HashMap<>();
	
	
	public List<Person> getdata(String paathADFile, String pathBWSFolder) throws IOException{
		
		List<Person> result= new ArrayList<>();
		File excelFile = new File(paathADFile);
		File bwsFolder= new File(pathBWSFolder);

		
		FileInputStream in_excel = new FileInputStream(excelFile);
		Workbook workbook = new XSSFWorkbook(in_excel);
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = datatypeSheet.iterator();

		boolean headline=true;
		Map<String,BWSResult> mothercodeToBWS=new HashMap<>();
		int z=0;
		int mothercodeIndex=0;
		int ageIndex = 0;
		int professionIndex=0;
		int genderIndex=0;
		int genderMeasureIndex=0;
		int eduIndex=0;
		
		/**
		 * BWS stuff
		 */
		for (File f: bwsFolder.listFiles()) {
			FileInputStream bwsExcel = new FileInputStream(f);
			Workbook bwsWB = new XSSFWorkbook(bwsExcel);
			Sheet bwsSheet = bwsWB.getSheetAt(0);
			Iterator<Row> bwsIterator = bwsSheet.iterator();	
			boolean firstLine= true;
			while (bwsIterator.hasNext()) {
	            Row row = bwsIterator.next();
	            if(firstLine) {
	            		firstLine=false;
	            		continue;
	            }
	            FrequencyDistribution<String> listBest= new FrequencyDistribution<String>();
				FrequencyDistribution<String> listWorst= new FrequencyDistribution<String>();
				
				Iterator<Cell> bewsCellIterator = row.iterator();
				boolean firstCell=true;
				boolean best=true;
				int counter=0;
				while (bewsCellIterator.hasNext()) {
					
					//break after bws cells
					counter++;
					if(counter>121) break;
					
					
					Cell currentCell = bewsCellIterator.next();
					
					
					// exclude timestamp cell
					if(firstCell) {
						firstCell=false;
					}else {
						if(best) {
//							System.out.println(currentCell.getStringCellValue());
							listBest.inc(currentCell.getStringCellValue());
							best=false;
						}else {
							listWorst.inc(currentCell.getStringCellValue());
							best=true;
						}
							
					}
				}
				String mothercodeBWS=row.getCell(122).getStringCellValue().toUpperCase();
//	            System.out.println(mothercodeBWS);
				BWSResult bwsRes= new BWSResult(mothercodeBWS,listBest,listWorst);
				mothercodeToBWS.put(mothercodeBWS, bwsRes);
				
//				System.out.println(bwsRes.getListBest().getMostFrequentSamples(3));
			}
		}


		/**
		 * AD stuff
		 */
		while (iterator.hasNext()) {

            Row currentRow = iterator.next();
			Iterator<Cell> cellIterator = currentRow.iterator();

			if (headline) {
				headline = false;
				int index=0;
				int j=0;
				while (cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
					if (currentCell.getCellTypeEnum() == CellType.STRING) {
//						System.out.print(currentCell.getStringCellValue() + "|");
						if(currentCell.getStringCellValue().equals("Kontakt: Code")) {
							mothercodeIndex=index;
						}
						if(currentCell.getStringCellValue().equals("gnder: Maßnahmen zur Gleichstellung der Geschlechter sollten")) {
							genderMeasureIndex=index;
						}
						if(currentCell.getStringCellValue().equals("Beschäftigung")) {
							professionIndex=index;
						}
						if(currentCell.getStringCellValue().equals("Geschlecht")) {
							genderIndex=index;
						}
						if(currentCell.getStringCellValue().equals("Alter (direkt): Ich bin   ... Jahre")) {
							ageIndex=index;
						}
						if(currentCell.getStringCellValue().equals("Formale Bildung (einfach)")) {
							eduIndex=index;
						}
						if(assertion(currentCell.getStringCellValue())) {
							varToIndex.put(assertionText(currentCell.getStringCellValue()), index);
//							System.out.println(j++ +" "+assertionText(currentCell.getStringCellValue()+" "+ index));
						}
					}
					index++;
				}
				}else {
					
					if(currentRow.getCell(0)  == null || currentRow.getCell(ageIndex)==null) {
						break;
					}
					Person p= new Person();
					
					p.setAge(currentRow.getCell(ageIndex).getNumericCellValue());
					p.setEdu(currentRow.getCell(eduIndex).getNumericCellValue());
					p.setGender(currentRow.getCell(genderIndex).getNumericCellValue());
					p.setGenderMeasures(currentRow.getCell(genderMeasureIndex).getNumericCellValue());
					p.setMothercode(currentRow.getCell(mothercodeIndex).getStringCellValue().toUpperCase());
					p.setProfession(currentRow.getCell(professionIndex).getNumericCellValue());
					
	//				System.out.println(p.print());
					/*
					 * Bind BWS and AD together
					 */
					System.out.println(p.getMothercode()+" "+p.getGender());
	//				System.out.println(mothercodeToBWS.get(p.getMothercode()).getListBest().getSampleWithMaxFreq());
					p.setBWSRes(mothercodeToBWS.get(p.getMothercode()));
					
					Map<String,Double> varToValue=new HashMap<>();
	//				System.out.println(z++);
					
					for(String var: varToIndex.keySet()) {
						varToValue.put(var, currentRow.getCell(varToIndex.get(var)).getNumericCellValue());
					}
					p.setVarToJudgments(varToValue);
					result.add(p);
	//				System.out.println();
				}
				
	        }
		return result;
	}
	
	
//	public static void main(String[] args) throws IOException {
//
//		File excelFile = new File("/Users/michael/Dropbox/gender_hatespeech_corpus/collected_data_raw/data_FrauenAD_2018-04-30_09-43.xlsx");
//		File bwsFolder= new File("/Users/michael/Dropbox/gender_hatespeech_corpus/collected_data_raw/bws_questionnaires");
//
//		FileInputStream in_excel = new FileInputStream(excelFile);
//		Workbook workbook = new XSSFWorkbook(in_excel);
//		Sheet datatypeSheet = workbook.getSheetAt(0);
//		Iterator<Row> iterator = datatypeSheet.iterator();
//
//		boolean headline=true;
//		
//		Map<String,BWSResult> mothercodeToBWS=new HashMap<>();
//		int z=0;
//		int mothercodeIndex=0;
//		int ageIndex = 0;
//		int professionIndex=0;
//		int genderIndex=0;
//		int genderMeasureIndex=0;
//		int eduIndex=0;
//		
//		/**
//		 * BWS stuff
//		 */
//		for (File f: bwsFolder.listFiles()) {
//			FileInputStream bwsExcel = new FileInputStream(f);
//			Workbook bwsWB = new XSSFWorkbook(bwsExcel);
//			Sheet bwsSheet = bwsWB.getSheetAt(0);
//			Iterator<Row> bwsIterator = bwsSheet.iterator();	
//			boolean firstLine= true;
//			while (bwsIterator.hasNext()) {
//	            Row row = bwsIterator.next();
//	            if(firstLine) {
//	            		firstLine=false;
//	            		continue;
//	            }
//	            FrequencyDistribution<String> listBest= new FrequencyDistribution<String>();
//				FrequencyDistribution<String> listWorst= new FrequencyDistribution<String>();
//				
//				Iterator<Cell> bewsCellIterator = row.iterator();
//				boolean firstCell=true;
//				boolean best=true;
//				int counter=0;
//				while (bewsCellIterator.hasNext()) {
//					
//					//break after bws cells
//					counter++;
//					if(counter>121) break;
//					
//					
//					Cell currentCell = bewsCellIterator.next();
//					
//					
//					// exclude timestamp cell
//					if(firstCell) {
//						firstCell=false;
//					}else {
//						if(best) {
////							System.out.println(currentCell.getStringCellValue());
//							listBest.inc(currentCell.getStringCellValue());
//							best=false;
//						}else {
//							listWorst.inc(currentCell.getStringCellValue());
//							best=true;
//						}
//							
//					}
//				}
//				String mothercodeBWS=row.getCell(122).getStringCellValue().toUpperCase();
////	            System.out.println(mothercodeBWS);
//				BWSResult bwsRes= new BWSResult(mothercodeBWS,listBest,listWorst);
//				mothercodeToBWS.put(mothercodeBWS, bwsRes);
//				
////				System.out.println(bwsRes.getListBest().getMostFrequentSamples(3));
//			}
//		}
//
//
//		/**
//		 * AD stuff
//		 */
//		while (iterator.hasNext()) {
//
//            Row currentRow = iterator.next();
//			Iterator<Cell> cellIterator = currentRow.iterator();
//
//			if (headline) {
//				headline = false;
//				int index=0;
//				int j=0;
//				while (cellIterator.hasNext()) {
//					Cell currentCell = cellIterator.next();
//					if (currentCell.getCellTypeEnum() == CellType.STRING) {
////						System.out.print(currentCell.getStringCellValue() + "|");
//						if(currentCell.getStringCellValue().equals("Kontakt: Code")) {
//							mothercodeIndex=index;
//						}
//						if(currentCell.getStringCellValue().equals("gnder: Maßnahmen zur Gleichstellung der Geschlechter sollten")) {
//							genderMeasureIndex=index;
//						}
//						if(currentCell.getStringCellValue().equals("Beschäftigung")) {
//							professionIndex=index;
//						}
//						if(currentCell.getStringCellValue().equals("Geschlecht")) {
//							genderIndex=index;
//						}
//						if(currentCell.getStringCellValue().equals("Alter (direkt): Ich bin   ... Jahre")) {
//							ageIndex=index;
//						}
//						if(currentCell.getStringCellValue().equals("Formale Bildung (einfach)")) {
//							eduIndex=index;
//						}
//						if(assertion(currentCell.getStringCellValue())) {
//							varToIndex.put(assertionText(currentCell.getStringCellValue()), index);
////							System.out.println(j++ +" "+assertionText(currentCell.getStringCellValue()+" "+ index));
//						}
//					}
//					index++;
//				}
//				
//			}else {
//				Person p= new Person();
//				
//				p.setAge(currentRow.getCell(ageIndex).getNumericCellValue());
//				p.setEdu(currentRow.getCell(eduIndex).getNumericCellValue());
//				p.setGender(currentRow.getCell(genderIndex).getNumericCellValue());
//				p.setGenderMeasures(currentRow.getCell(genderMeasureIndex).getNumericCellValue());
//				p.setMothercode(currentRow.getCell(mothercodeIndex).getStringCellValue().toUpperCase());
//				p.setProfession(currentRow.getCell(professionIndex).getNumericCellValue());
//				
////				System.out.println(p.print());
//				/*
//				 * Bind BWS and AD together
//				 */
////				System.out.println(p.getMothercode()+" "+p.getGender());
////				System.out.println(mothercodeToBWS.get(p.getMothercode()).getListBest().getSampleWithMaxFreq());
//				p.setBWSRes(mothercodeToBWS.get(p.getMothercode()));
//				
//				Map<String,Double> varToValue=new HashMap<>();
////				System.out.println(z++);
//				if(currentRow.getCell(0)==null) {
//					break;
//				}
//				for(String var: varToIndex.keySet()) {
//					varToValue.put(var, currentRow.getCell(varToIndex.get(var)).getNumericCellValue());
//				}
////				System.out.println();
//			}
//			
//        }
//	}


	private static String assertionText(String stringCellValue) {
		String newString= stringCellValue.split("agree\\d:")[1];
		newString=newString.split(" Reaktionszeit [ms]")[0];
		return newString;
	}

	private static boolean assertion(String stringCellValue) {
		if(stringCellValue.startsWith("agree") && !stringCellValue.endsWith("[ms]")) {
			return true;
		}
		return false;
	}



	public Map<String, Integer> getAssertionsToIndex() {
		return varToIndex;
	}

}
