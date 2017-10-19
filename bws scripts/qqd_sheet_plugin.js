function onOpen() {
  SpreadsheetApp.getUi() // Or DocumentApp or FormApp.
      .createMenu('QQD')
      .addItem('start new', 'startNew').addSeparator().addItem('generate judgments survey', 'generateJudgmentsSurvey').addToUi();
}

function startNew() {
  var ui = SpreadsheetApp.getUi(); 

  var result = ui.prompt(
      'What issue do you wnat to understand?',
      ui.ButtonSet.OK_CANCEL);

  var button = result.getSelectedButton();
  var text = result.getResponseText();
  if (button == ui.Button.OK) {
    ui.alert('Generate surveys for ' + text + '.');
  } 
  var creatingForm= createAssertionsForm(text);
  
  var sheet = SpreadsheetApp.getActiveSheet();
  sheet.appendRow([text, creatingForm.getId(),creatingForm.getDestinationId(),creatingForm.getPublishedUrl(),creatingForm.getEditUrl()]);
}

function createAssertionsForm(issue) {
  var item = "Creating Assertions on "+ issue;  
  var form = FormApp.create(item).setTitle(item);
  
  form.setDescription("Dear Colleagues,\n [...] We want to test a newly developed process that aims at quickly summarising all conflicting or" 
  + " or  blub blub \n Please give us some assertions on the stuff. \n "
  + "The assertions must be expressions that one can agree or disagree with. They can be claims, beliefs, opinions, reasons, arguments, or any statement that can be used to inform or support one's position on the issue.\n "
  +"The assertions do not have to be reflective of your own opinions. In fact we would prefer a diverse set of assertions corresponding to a diverse set of positions on the issue. The assertions can be about a sub-issue or an aspect of the issue."
  +"The assertions should: \n"
  +"\n"
  +" - support a position that is relevant to the issue. \n"
  +"- cover a diverse set of positions on the issue. (Avoid claims that rephrase the same argument in slightly different ways.) \n"
  +"- be formulated in a way that a third person can agree or contradict the assertion.\n"
  +"- be self contained and understandable without additional context. (Do not use 'it', 'she/her' or 'he/him/his' etc. to refer to an issue, a person or something else that is not directly mentioned in your assertion.)\n"
  +"- be precise. (Avoid vague formulations such as maybe, perhaps, presumably or possibly.)\n"
  +"\n"
  +"The assertions should NOT:\n"
  +"\n"
  +"- be a simple expression of agreeing/supporting or disagreeing/rejecting the overall issue.\n"
  +"- contain multiple positions (e.g. migrants  are friendly and hardworking).\n"
  +"- contain expressions of personal perspective (e.g. I don't like immigrants).\n"
  +"\n"
  +"You do not have to agree with a assertion yourself. Include the assertion if you think many people think that assertion is valid and important in the controversy.\n"
  +"One way to come up with assertions is to think about the issue from the perspective of the economy, morality, fairness, constitutionality, law and order, security, health, culture, etc. You are not restricted to them.\n"
  +"\n"
  +"[EXAMPLES]\n"
  +"\n"
  +"Give us as many assertions as you want. If you want to submit more than 5 assertions re-vist the formular multiple times. ");
  
  item = "Assertion 1";  
  form.addTextItem().setTitle(item); 
  item = "Assertion 2";  
  form.addTextItem().setTitle(item);
  item = "Assertion 3";  
  form.addTextItem().setTitle(item);
  item = "Assertion 4";  
  form.addTextItem().setTitle(item);
  item = "Assertion 5";  
  form.addTextItem().setTitle(item);
  
  var ss = SpreadsheetApp.create('Judging Assertions on '+issue+' (Answers)');
  form.setDestination(FormApp.DestinationType.SPREADSHEET, ss.getId());
  
  return form;
}

function generateJudgmentsSurvey(){  
  var sheet = SpreadsheetApp.getActiveSheet();
  var data = sheet.getDataRange().getValues();
  
  var activeRow= sheet.getActiveCell().getRow();
 // SpreadsheetApp.getUi().alert(activeRow);
 // SpreadsheetApp.getUi().alert(data[activeRow-1][0]);
  if((typeof data[activeRow-1] !== "undefined" && typeof data[activeRow-1][0] !== "undefined") || activeRow==1){
    var issue=data[activeRow-1][0];
    var judgmentForm= createJugmentForm(data[activeRow-1][0],data[activeRow-1][2]);
    var cell = sheet.getRange(activeRow,6).setValue(judgmentForm.getPublishedUrl());
    var cell = sheet.getRange(activeRow,7).setValue(judgmentForm.getEditUrl());
  }else{
    SpreadsheetApp.getUi().alert("Please select an issue.");
  }
  
}


function createJugmentForm(issue, assertionsSheetId) {  
  // create & name Form  
  var item = "Judging Assertions on "+ issue;   
  var form = FormApp.create(item).setTitle(item); 
  
  form.setProgressBar(true);
  form.setCustomClosedFormMessage("Thank you for helping us understanding "+issue);
  //form.setShuffleQuestions(true);
  
  var assertionsSheet = SpreadsheetApp.openById(assertionsSheetId);
  var assertions = getAssertions(assertionsSheet);
  var paginationCounter1=0;
  var AgreeDisagreeChoices = ["agree", "disagree"];
  for (var a = 0; a < assertions.length; a++) {
    paginationCounter1++;
    if(paginationCounter1>4){
      form.addPageBreakItem().setTitle('Agree/Disagree');
      paginationCounter1=0;
    }
    form.addMultipleChoiceItem().setTitle(assertions[a]).setChoiceValues(AgreeDisagreeChoices).setRequired(false);
  }
 
  form.addPageBreakItem().setTitle('Amount of Support');
  var bwsTuples=createBWSTuples(assertions,1,4,100);
  var paginationCounter2=0;
  for (var i = 0; i < bwsTuples.length; i++) {
    paginationCounter2++;
    if(paginationCounter2>4){
      form.addPageBreakItem().setTitle('Amount of Support');
      paginationCounter2=0;
    }
    form.addSectionHeaderItem().setTitle("Compare the assertions");
    var choices = bwsTuples[i].split("\t");
    form.addMultipleChoiceItem().setTitle("Which of these assertions on the issue "+issue+" do you support the most (or oppose the least)?").setChoiceValues(choices).setRequired(false);
    form.addMultipleChoiceItem().setTitle("Which of these assertions on the issue "+issue+" do you oppose the most (or support the least)?").setChoiceValues(choices).setRequired(false);
  }
  
  var ss = SpreadsheetApp.create('Judging Assertions on '+issue+' (Answers)');
  form.setDestination(FormApp.DestinationType.SPREADSHEET, ss.getId());
  
  return form;
 }  

function getAssertions(assertionSheet){
  var data = assertionSheet.getDataRange().getValues();
  var assertions = [];
  //start from 1 to ignore deadlines
  for (var i = 1; i < data.length; i++) {
    assertions.push(data[i][1]);
    assertions.push(data[i][2]);
    assertions.push(data[i][3]);
    assertions.push(data[i][4]);
    assertions.push(data[i][5]);
  }
  
  return unique(assertions);
}

function unique(array) {
  var prims = {"boolean":{}, "number":{}, "string":{}}, objs = [];
  return array.filter(function(item) {
     var type = typeof item;
   if(type in prims){
     return prims[type].hasOwnProperty(item) ? false : (prims[type][item] = true);
     }
    else{
     return objs.indexOf(item) >= 0 ? false : objs.push(item);
    }     
  });
}


// calculate the standard deviation of a set of values
function stdev(array){
  var n = array.length;
  if(n == 1) {
	return 0;
  }
  var m = mean(array);
  var SDprep = 0;
  for(var key in array){
    SDprep += Math.pow((parseFloat(array[key]) - m),2);
  }
  return Math.sqrt(SDprep/array.length);
}  
 
  
// calculate the mean of a set of values
function mean(array) {
  var sum = 0;
  for( var i = 0; i < array.length; i++ ){
    sum += array[i]
  }
  return sum/array.length;
}  
    
function createNewTuple(array,n){
  var selectedItems=[];
  for(var i = 0; i < n; i++ ){
    var newArray=shuffle(array);
    selectedItems.push(newArray[0]);
    //make sure item is only picked once
    array.shift();
  }
  return selectedItems;
}  

function addToMap(map,stringv){
  if (stringv in map) {
   var newcount= map[stringv]+1;
   map[stringv]=newcount;
  } else {
   map[stringv]=1; 
  }
  return map;
}  

function shuffle(array) {
  var currentIndex = array.length, temporaryValue, randomIndex;

  while (0 !== currentIndex) {
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}

function getValues(map){
  var map_values = [];
  for (var key in map) {
    var d =map[key];
    map_values.push(map[key]);
  }
  return map_values;
}

//number of items per tuple (typically, 4 or 5)
//Best-Worst Scaling factor (typically 1.5 or 2):
//multiply the number of items in $file_items by this factor
//in order to determine the number of tuples to generate
// number of iterations (typically 100 or 1000)
function createBWSTuples(assertions,factor,items_per_tuple,num_iter){

  var num_items = assertions.length;
  var num_unique_pairs = (num_items * (num_items - 1)) / 2;
  
  if(num_items < items_per_tuple) {
    return undefined;
  }
  
  //generate tuples
  var num_tuples = Math.floor(0.5 + (factor * num_items));

  // try $num_iter different randomizations
  var best_score;
  var best_tuples = [];

  for (var iter = 1; iter <= num_iter; iter++) {

	// generate $num_tuples tuples by randomly sampling without replacement
    var tuples = [];
	var ranlist = shuffle(assertions);     // make a random list of items
    var freq_pair = {};
	
	var j = 0;   // index of the current item in the random list
	for (var i = 0; i < num_tuples; i++) {
	
		var tuple = [];   //new tuple
		
		// check if we have enough remaining items in the random list to form a new tuple
		if((j + items_per_tuple) <= ranlist.length) {
		
		// form a new tuple with $items_per_tuple items in the random list starting at index $j
        var newtuple= createNewTuple(ranlist.slice(j, ranlist.length),items_per_tuple);
        for (var t_i = 0; t_i < newtuple.length; t_i++) {
          tuple.push(newtuple[t_i]);
        }  
        
        //tuple.push(createNewTuple(ranlist.slice(j, ranlist.length),items_per_tuple));
        j += items_per_tuple;
			
		} 
      else {
        // get the rest of the list
		var need_more = items_per_tuple - ranlist.length + j;  // the number of items that we will need to get from a new random list
		for(var z=j; z < ranlist.length; z++) {
           tuple.push(ranlist[z]);
		}
		// generate a new random list of items
		var newranlist = shuffle(assertions);
		for(var z=0;z < need_more; z++) {
          // if a item is already in the tuple, move it to the end of the list
          while(ranlist[z] in tuple) {
             var h = newranlist[z];
             newranlist.splice(z, 1);
             newranlist.push(h);
			}
          tuple.push(newranlist[z]);
        }			
      }
		
      var tuple_string = tuple.join("\t");
      tuples.push(tuple_string);

      // add frequencies of pairs of items
      for(var k1 = 0; k1 < tuple.length; k1++) {
		for(var k2 = k1+1; k2 < tuple.length; k2++) {
			if(tuple[k1] > tuple[k2]) {
               addToMap(freq_pair,tuple[k1]+"::"+tuple[k2])
			} else{
               addToMap(freq_pair,tuple[k2]+"::"+tuple[k1])
			}
		}
      }
	}	

	// calculate the two-way balance of the set of tuples
	var freq_pair_values = getValues(freq_pair);
	var stddev_pairs = stdev(freq_pair_values);

	// calculate the score for the set and keep the best score and the best set
	var score = stddev_pairs;
  
	if ((iter == 1) || (score < best_score)) {
		best_score = score;
		best_tuples = tuples;
	}  
  }
  return best_tuples;
} 


function test(){
  var fruits = ["Banana", "Orange", "Apple", "Mango","Cherry", "Lemon", "Strawberry", "Pinapple", "Coconut","Banana1", "Orange1", "Apple2", "Mango3","Cherry4", "Lemon5", "Strawberry1", "Pinapple1", "Coconut1"];
  var x= createBWSTuples(fruits,1,4,100);
  var z ="";
}