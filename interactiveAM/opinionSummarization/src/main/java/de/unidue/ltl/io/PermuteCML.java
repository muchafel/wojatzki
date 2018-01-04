package de.unidue.ltl.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermuteCML {

	public static void main(String[] args) {
		Set<String> result = new HashSet<String>();
		List<String> agree = new ArrayList<>(Arrays.asList("ad1:[agree]", "ad2:[agree]", "ad3:[agree]", "ad4:[agree]"));
		List<String> disagree = new ArrayList<>(
				Arrays.asList("ad1:[disagree]", "ad2:[disagree]", "ad3:[disagree]", "ad4:[disagree]"));
		int j = 0;
		List<String> tempList1= new ArrayList<>();
		List<String> tempList2= new ArrayList<>();
		List<String> tempList3= new ArrayList<>();
		List<String> tempList4= new ArrayList<>();
		String toAdd2 = disagree.get(0);
		String toAdd3 = disagree.get(0);
		String toAdd4 = agree.get(0);
		for (String jj : disagree) {
			if (j == 0) {
				tempList1.add(disagree.get(j));
				tempList1.add(agree.get(j));
			}
			if (j == 1) {
				for(String t:tempList1){
					tempList2.add(t+"++"+agree.get(j));
					tempList2.add(t+"++"+disagree.get(j));
				}
			}
			if (j == 2) {
				for(String t:tempList2){
					tempList3.add(t+"++"+agree.get(j));
					tempList3.add(t+"++"+disagree.get(j));
				}
			}
			if (j == 3) {
				for(String t:tempList3){
					tempList4.add(t+"++"+agree.get(j));
					tempList4.add(t+"++"+disagree.get(j));
				}
			}
			j++;
		}
		result.addAll(tempList4);
//		System.out.println(result.size());
		int i=0;
		for(String a: result){
			System.out.println();
			System.out.println("<cml:radios label=\"Q2: Which agree/disagree judgement do you feel *most* passionate about? \" validates=\"required\" gold=\"true\" only-if=\""+a+"\" name=\"q2 "+i+"\" >");
			System.out.println("<cml:radio label=\""+a.split("\\+\\+")[0].split(":")[1]+" Assertion 1: {{tuple_item_a}}\" value=\"assertion_a\"/> \n "
					+ "<cml:radio label=\""+a.split("\\+\\+")[1].split(":")[1]+" Assertion 2: {{tuple_item_b}}\" value=\"assertion_b\" /> \n"
					+ "<cml:radio label=\""+a.split("\\+\\+")[2].split(":")[1]+" Assertion 3: {{tuple_item_c}}\" value=\"assertion_c\" /> \n"
					+ "<cml:radio label=\""+a.split("\\+\\+")[3].split(":")[1]+" Assertion 4: {{tuple_item_d}}\" value=\"assertion_d\" />\n"
					+ "</cml:radios>");
			System.out.println();
			i++;
		}
		int z=0;
		for(String a: result){
			System.out.println();
			System.out.println("<cml:radios label=\"Q3: Which agree/disagree judgement do you feel *least* passionate about? \" validates=\"required\" gold=\"true\" only-if=\""+a+"\" name=\"q3 "+z+"\">");
			System.out.println("<cml:radio label=\""+a.split("\\+\\+")[0].split(":")[1]+" Assertion 1: {{tuple_item_a}}\" value=\"assertion_a\"/> \n "
					+ "<cml:radio label=\""+a.split("\\+\\+")[1].split(":")[1]+" Assertion 2: {{tuple_item_b}}\" value=\"assertion_b\" /> \n"
					+ "<cml:radio label=\""+a.split("\\+\\+")[2].split(":")[1]+" Assertion 3: {{tuple_item_c}}\" value=\"assertion_c\" /> \n"
					+ "<cml:radio label=\""+a.split("\\+\\+")[3].split(":")[1]+" Assertion 4: {{tuple_item_d}}\" value=\"assertion_d\" />\n"
					+ "</cml:radios>");
			System.out.println();
			z++;
		}
	}

}
