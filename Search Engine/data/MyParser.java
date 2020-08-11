package com.xinyuan.first_tika;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;  
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.tika.exception.TikaException;  
import org.apache.tika.metadata.Metadata;  
import org.apache.tika.parser.ParseContext;  
import org.apache.tika.parser.html.HtmlParser;  
import org.apache.tika.sax.BodyContentHandler;  
import org.xml.sax.SAXException;

public class MyParser {  
	public static void main(final String[] args) throws IOException,SAXException, TikaException {

		File dir = new File("C:\\Users\\Xinyuan\\Desktop\\hw4\\nytimes\\nytimes");
		PrintWriter pw = new PrintWriter(new File("C:\\xampp\\htdocs\\HW4\\big.txt"));
		int cnt = 0;
		for (File file : dir.listFiles()) {
			//detecting the file type
		      BodyContentHandler handler = new BodyContentHandler(-1);
		      Metadata metadata = new Metadata();
		      FileInputStream inputstream = new FileInputStream(file);
		      ParseContext pcontext = new ParseContext();
		      
		      //Html parser 
		      HtmlParser htmlparser = new HtmlParser();
		      htmlparser.parse(inputstream, handler, metadata,pcontext);
		      pw.println(handler.toString());
		      System.out.println(++cnt);
		}
		pw.close();
	}
}  