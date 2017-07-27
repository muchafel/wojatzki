package de.uni.due.ltl.interactiveStance.analyzer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Sorter;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;

import de.uni.due.ltl.interactiveStance.backend.ExplicitTarget;
import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class TargetSearcher {
	private Directory index;
	private StandardAnalyzer analyzer;
	private int hitsPerPage;
	
	public void SetUp(StanceDB db, int hitLimit) throws SQLException, IOException{
		
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();
		this.hitsPerPage=hitLimit;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        db.setUpIndex(w);
		w.close();
		
	}
	
	public List<ExplicitTarget> search(String query, boolean doSort, HashMap<String, ExplicitTarget> selectedFavorTargets, HashMap<String, ExplicitTarget> selectedAgainstTargets) throws IOException, ParseException{
		List<ExplicitTarget> result= new ArrayList<>();
		QueryParser parser= new QueryParser("name", analyzer);
		parser.setAllowLeadingWildcard(true);
		Query q = parser.parse("*"+query+"*");

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
      
        //get a list of all IDs that have been selected
        Set<String> selectedIds= getSelectedIds(selectedFavorTargets,selectedAgainstTargets);
        
        TopDocs docs= null;
        
        if(doSort){
        	SortField longSort = new SortedNumericSortField("instanceCount", SortField.Type.INT, true);
            Sort sort = new Sort(longSort);
            docs = searcher.search(q,hitsPerPage,sort);
        }else{
        	docs = searcher.search(q,hitsPerPage);
        }
        
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " of allowed "+hitsPerPage);
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            
            //only add the docs if the target is already selected
            if(!selectedIds.contains(d.get("id"))){
            	 System.out.println((i + 1) + ".  " + d.get("name")+" ("+d.get("id")+")" +" : "+d.get("website")+  " : "+ d.get("instanceCount")+" (+"+d.get("favorCount")+"/-"+d.get("againstCount")+")");
                 ExplicitTarget explicitTarget= new ExplicitTarget(d.get("id"), d.get("name"), Integer.valueOf(d.get("favorCount")), Integer.valueOf(d.get("againstCount")));
                 result.add(explicitTarget);
            }
        }

        reader.close();
		
		return result;
	}

	/**
	 * returns a list of all IDs that are contained in the selectedFavor and selectedAgainst list
	 * @param selectedFavorTargets
	 * @param selectedAgainstTargets
	 * @return
	 */
	private Set<String> getSelectedIds(HashMap<String, ExplicitTarget> selectedFavorTargets,HashMap<String, ExplicitTarget> selectedAgainstTargets) {
		Set<String> selectedIds = new HashSet<>();
		selectedIds.addAll(selectedFavorTargets.keySet());
		selectedIds.addAll(selectedAgainstTargets.keySet());
		return selectedIds;
	}
	

}
