package com.xinyuan.my_jsoup;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EdgeList {
	
	private Map<String, String> fileUrlMap = new HashMap<>();
	private Map<String, String> urlFileMap = new HashMap<>();
	
	private void genMaps(String csvPath) {
		try (Scanner input = new Scanner(new File(csvPath));) {
			boolean header = true;
			while (input.hasNext()) {
				String line = input.nextLine();
				if (header) {
					header = false;
				}
				else {
					String[] tmp = line.split("[,]+");
					fileUrlMap.put(tmp[0], tmp[1]);
					urlFileMap.put(tmp[1], tmp[0]);
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void writeFile(String txtPath, Set<String> edges) {
		try (PrintWriter output = new PrintWriter(new File(txtPath));) {
			for (String edge : edges) {
				output.println(edge);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void genGraph(String dirPath, String csvPath, String txtPath) {
		genMaps(csvPath);
		File dir = new File(dirPath);
		Set<String> edges = new HashSet<String>();
		try {
			int cnt = 0;
			for (File file : dir.listFiles()) {
				Document doc = Jsoup.parse(file, "UTF-8", fileUrlMap.get(file.getName()));
				Elements links = doc.select("a[href]");
				
				for (Element link : links) {
					String url = link.attr("abs:href").trim();
					if (urlFileMap.containsKey(url)) {
						edges.add(file.getName() + " " + urlFileMap.get(url));
					}
				}
				cnt++;
				System.out.println("Processed: " + cnt);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			writeFile(txtPath, edges);
		}
	}
}
