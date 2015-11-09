/*
This file takes as input the uniprot xml file,and prints the corresponding sequence found in uniprot fasta file for every entry in the xml file.
It runs by executing the file found fastaparser.py in the same directory.

Arguments:- uniprot-xml-database filename and uniprot-fasta-database-filename

Ex:-

java xml_fasta_linker.java uniprot_sprot.xml uniprot_sprot.fasta
*/
import java.io.*;
 
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

class ExecuteShellComand {
 	
 	public String executeCommand(String command) { 
		StringBuffer output = new StringBuffer(); 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
 			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString(); 
	}

}

public class xml_fasta_linker {

	public static void main(String[] args) {
		
		//Creating a document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder;     
	    Document doc = null;
		
		//Setting the values for identifying each entry
		String currentLine=null,gene=null,prot_id=null;
		String sourceFileName=args[0];//Usually "uniprot_sprot.xml"
		String fastaFileName=args[1];
		String openFileName=null;
		String start="<entry";
		String stop="</entry";
		int fileIter=1;
		boolean success;
		boolean exists=true;
		String sequence;
		openFileName=Integer.toString(fileIter).concat(".xml");		
		
		try {
	 		//Initializing file i/o
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
			            prot_id=getAccession(doc,xpath);
			            ExecuteShellComand obj=new ExecuteShellComand();
			            //System.out.println("python fastaparser.py "+fastaFileName+" "+prot_id); //For testing
						sequence=obj.executeCommand("python fastaparser.py "+fastaFileName+" "+prot_id);
						System.out.println(prot_id+"\n"+sequence);	        
			        }
			        catch (ParserConfigurationException | SAXException | IOException e) {
			            e.printStackTrace();
			        }
					success = (new File(openFileName)).delete();
					//Deleted file 'openFileName'
					fileIter=fileIter+1;

					/* //Used for testing
					if(fileIter==2)
						break;
					*/
						
					openFileName=Integer.toString(fileIter).concat(".xml");
					openFileWriter = new PrintWriter(openFileName, "UTF-8");
			    }
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getAccession(Document doc, XPath xpath) {
        String result=null;
            try{
                XPathExpression expr = xpath.compile("/entry/accession/text()");
                result = (String) expr.evaluate(doc, XPathConstants.STRING);
                } 
            catch (XPathExpressionException e) {
                e.printStackTrace();
                } 
        return result;
    }
}