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

public class protSplitter {
	public static void main(String[] args) {
		//Creating a document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder;     
	    Document doc = null;
		//Setting the values for identifying each entry
		String currentLine=null;
		String sourceFileName="uniprot_sprot.xml";
		String openFileName=null;
		String start="<entry";
		String stop="</entry";
		int fileIter=1;
		boolean success;
		openFileName=Integer.toString(fileIter).concat(".xml");		
		try {
			BufferedReader sourceFileBuffer = new BufferedReader (new InputStreamReader(new FileInputStream(sourceFileName)));
			PrintWriter openFileWriter = new PrintWriter(openFileName, "UTF-8");
			//Looping through the source file
			while((currentLine=sourceFileBuffer.readLine())!=null) {
				if(currentLine.substring(0,6).equals(start)) {
					openFileWriter.write("<?xml version=\"1.0\"?>\n");
					openFileWriter.write(currentLine);
					openFileWriter.write('\n');
					currentLine=sourceFileBuffer.readLine();
					while(!currentLine.substring(0,7).equals(stop)) {
						openFileWriter.write(currentLine);
						openFileWriter.write('\n');
						currentLine=sourceFileBuffer.readLine();
					}
					openFileWriter.write(currentLine);
					openFileWriter.write('\n');
					openFileWriter.close();
					//Temporary file created with name 'openFileName'
					try {
			            builder = factory.newDocumentBuilder();
			            doc = builder.parse(openFileName);
			            XPathFactory xpathFactory = XPathFactory.newInstance();
			            XPath xpath = xpathFactory.newXPath();
			            String gene = getGeneId(doc,xpath);
			            System.out.println(fileIter+"---"+gene);
			        }
			        catch (ParserConfigurationException | SAXException | IOException e) {
			            e.printStackTrace();
			        }
			        //if(fileIter==5)
			        //	break;
					success = (new File(openFileName)).delete();
					//System.out.println(fileIter+" completed");
					fileIter=fileIter+1;
                    //break;
					openFileName=Integer.toString(fileIter).concat(".xml");
					openFileWriter = new PrintWriter(openFileName, "UTF-8");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();;
		}
	}

	private static String getGeneId(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/entry/dbReference[@type='GeneID']/@id");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }
}