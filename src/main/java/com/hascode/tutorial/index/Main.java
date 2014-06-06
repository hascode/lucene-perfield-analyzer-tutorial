package com.hascode.tutorial.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.hascode.tutorial.index.analysis.ECharacterAnalyser;

public class Main {
	private final Version version = Version.LUCENE_48;

	public void run() throws IOException {
		Directory index = new RAMDirectory();
		Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
		analyzerPerField.put("email", new KeywordAnalyzer());
		analyzerPerField.put("specials", new ECharacterAnalyser(version));
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
				new StandardAnalyzer(version), analyzerPerField);
		IndexWriterConfig config = new IndexWriterConfig(version, analyzer)
				.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(index, config);
		Document doc = new Document();
		doc.add(new StringField("author", "kitty", Store.YES));
		doc.add(new TextField(
				"content",
				"Oh hai. In teh beginnin Ceiling Cat maded teh skiez An da Urfs, but he did not eated dem.",
				Store.YES));
		doc.add(new StringField("email", "kitty@cat.com", Store.YES));
		doc.add(new StringField("specials", "13e12e22e45e66", Store.YES));
		writer.addDocument(doc);
		writer.commit();
		writer.close();

		int limit = 20;
		Query query = new TermQuery(new Term("author", "kitty"));
		try (IndexReader reader = DirectoryReader.open(index)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			TopDocs docs = searcher.search(query, limit);

			System.out.println(docs.totalHits + " found for query: " + query);

			for (final ScoreDoc scoreDoc : docs.scoreDocs) {
				System.out.println(searcher.doc(scoreDoc.doc));
			}
		}

		index.close();
	}

	public static void main(String[] args) throws IOException {
		new Main().run();
	}
}
