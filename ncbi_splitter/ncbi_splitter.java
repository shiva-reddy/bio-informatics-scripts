/* This program takes ncbi gene database as input ,fetches and prints the following details.
(Update when modified)
1.Official symbol
2.Official Name
3.Gene id
4.Gene type
5.Organism name
6.Organism Lineage
7.Summary
8.Alias list
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class jsplitter {
	public static void main(String[] args) {
		String currentLine=null;
		String sourceFileName=args[0];//The file name of the ncbi database
		String openFileName=null;

        //Parameters for breaking the total database into seperate chunks,Each representing one gene
		String start="  <Entrezgene>";
		String stop="  </Entrezgene>";
		int fileIter=1;//Used for counting the number of genes 
		boolean success;

		openFileName="ncbi_splitter_temp_file.xml";//Temporary file, stores the xml for a single gene,gets destroyed after the processing for the gene is finished
		try {
			BufferedReader sourceFileBuffer = new BufferedReader (new InputStreamReader(new FileInputStream(sourceFileName)));
			PrintWriter openFileWriter = new PrintWriter(openFileName, "UTF-8");
            pw = new PrintWriter (fw);
			while((currentLine=sourceFileBuffer.readLine())!=null) {
				if(currentLine.equals(start)) {
					openFileWriter.write("<?xml version=\"1.0\"?>\n");
					openFileWriter.write(currentLine);
					openFileWriter.write('\n');
					currentLine=sourceFileBuffer.readLine();
					while(!currentLine.equals(stop)) {
						openFileWriter.write(currentLine);
						openFileWriter.write('\n');
						currentLine=sourceFileBuffer.readLine();
					}
					openFileWriter.write(currentLine);
					openFileWriter.write('\n');
					openFileWriter.close();
                    
                    //A new file with name openFileName(jsplitter_temp_file.xml) has been created which stores the xml for the gene found in fileIter entry in the ncbi gene DB					
                    xParse(openFileName);
					//The file gets deleted after the parsing is finished
                    
                    success = (new File(openFileName)).delete();
					System.out.println(fileIter+" completed");
					fileIter=fileIter+1;
                    
                    /*//Used for testing
                    if(fileIter==2)
                        break;
					*/
                    openFileWriter = new PrintWriter(openFileName, "UTF-8");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();;
		}
		
	}
	private static void xParse(String fileName) {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);
	        DocumentBuilder builder;     
	        Document doc = null;            
	        try {
	            //Building aa document and an xpath object for parsing                
                builder = factory.newDocumentBuilder();
	            doc = builder.parse(fileName);
	            XPathFactory xpathFactory = XPathFactory.newInstance();
	            XPath xpath = xpathFactory.newXPath();
                //Fetching and printing ncbi parameters
	            System.out.println("--------------------------------------------------------");
	            String officialSymbol = getOfficialSymbol(doc, xpath);
	            String officialFullName = getOfficialFullName(doc,xpath);
	            String geneId = getGeneId(doc,xpath);
	            String geneType = getGeneType(doc,xpath);
	            String organismName = getOrganismName(doc,xpath);
	            String organismLineage = getOrganismLineage(doc,xpath);
	            String summary = getSummary(doc,xpath);
	            List<String> aliasList = getAliasList(doc,xpath);
	            System.out.println("The alias list is " + Arrays.toString(aliasList.toArray()));
	        }
	        catch (ParserConfigurationException | SAXException | IOException e) {
	            e.printStackTrace();
	        }
	}
	private static List<String> getAliasList(Document doc, XPath xpath) {
        List<String> list = new ArrayList<>();
        try {
            XPathExpression expr =
                xpath.compile("/Entrezgene/Entrezgene_gene/Gene-ref/Gene-ref_syn/Gene-ref_syn_E/text()");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++)
            {
                //System.out.println(nodes.item(i).getNodeValue());
                list.add(nodes.item(i).getNodeValue());
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String getGeneId(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_track-info/Gene-track/Gene-track_geneid/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getSummary(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_summary/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getOrganismLineage(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("Entrezgene/Entrezgene_source/BioSource/BioSource_org/Org-ref/Org-ref_orgname/OrgName/OrgName_lineage/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getOrganismName(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_source/BioSource/BioSource_org/Org-ref/Org-ref_taxname/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getGeneType(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_type/@value");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getOfficialFullName(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_gene/Gene-ref/Gene-ref_desc/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }

    private static String getOfficialSymbol(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/Entrezgene/Entrezgene_gene/Gene-ref/Gene-ref_locus/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                System.out.println(result);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }
}