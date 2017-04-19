package de.uni.due.ltl.interactiveStance.analyzer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import de.uni.due.ltl.interactiveStance.db.StanceDB;

public class TargetSearcher {
	private Directory index;
	private StandardAnalyzer analyzer;
	private int hitsPerPage = 100;
	
	public void SetUp(StanceDB db) throws SQLException, IOException{
		
		analyzer = new StandardAnalyzer();
		index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        db.setUpIndex(w);
//        int i=0;
//		for(String name: db.getModelNames()){
//			i++;
//			addTarget(w, name);
//		}
//		System.out.println(i);
		w.close();
		
	}
	
	public List<String> search(String query) throws IOException, ParseException{
		List<String> result= new ArrayList<>();
		QueryParser parser= new QueryParser("name", analyzer);
		parser.setAllowLeadingWildcard(true);
		Query q = parser.parse("*"+query+"*");

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        
        SortField longSort = new SortedNumericSortField("instanceCount", SortField.Type.INT, true);
        Sort sort = new Sort(longSort);
        TopDocs docs = searcher.search(q,hitsPerPage,sort);
        
//        TopDocs docs = searcher.search(q,hitsPerPage);
        
        
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " of allowed "+hitsPerPage);
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ".  " + d.get("name")+ " : "+d.get("website")+  " : "+ d.get("instanceCount"));
            result.add(d.get("name"));
        }

        reader.close();
		
		return result;
	}
	
	
	

	private void addTarget(IndexWriter w, String name) throws IOException {
		Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        w.addDocument(doc);
	}
}
