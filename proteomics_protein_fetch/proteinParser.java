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
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.sql.*;

public class proteinParser {
	private static final String url = "jdbc:mysql://localhost/proteomics";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   	//
   // private static final String user = "root";
   // private static final String password = "A!pink@elephant#";
	//
	public static void main(String[] args) {
		String tissueId=args[0];
		String user="root";
		String pass="pass";
		String fileName="temp.xml";
		try {
			System.out.println("-----------------------------------------------------------");
			System.out.println("Started parsing "+args[0]+"response..");
			xparse(fileName,tissueId,user,pass);
		}
		catch (Exception e) {
			e.printStackTrace();		
		}		
	}

	private static void xparse(String fileName,String tissueId,String user,String pass) {
		PrintWriter errorPrintWriter=null;
		try {
			errorPrintWriter= new PrintWriter (new FileWriter ("parseErrorLog.txt", true));
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	factory.setNamespaceAware(true);
	    	DocumentBuilder builder;     
	    	Document doc = null;	    
	        builder = factory.newDocumentBuilder();
	        doc = builder.parse(fileName);
	        XPathFactory xpathFactory = XPathFactory.newInstance();
	        XPath xpath = xpathFactory.newXPath();     
            XPathExpression expr = xpath.compile("/*[local-name()='feed']/*[local-name()='entry']/*[local-name()='content']/*[local-name()='properties']");
            NodeList entryNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
	        databaseAdd(entryNodes,tissueId,user,pass);
	        
	    }
	    catch (Exception e) {
	    	errorPrintWriter.write(tissueId+"\n");
	        e.printStackTrace();
	    }
	}
	
	public static void databaseAdd(NodeList entryNodes,String tissueId,String user,String pass){
		PrintWriter errorPrintWriter=null;
		try{
			errorPrintWriter = new PrintWriter (new FileWriter ("dbErrorLog.txt", true));
			Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            System.out.println("Success establishing db connection..");
            Statement stmt = con.createStatement();
            String sql="";
			for (int i = 0; i < entryNodes.getLength() ; i++) {
		        Node n = entryNodes.item(i);
		        if (n.getNodeType() == Node.ELEMENT_NODE) {
		        	Element e = (Element) n;
		        	String entryName = e.getElementsByTagName("d:ENTRY_NAME").item(0).getTextContent();
		        	String uniqueIdentifier = e.getElementsByTagName("d:UNIQUE_IDENTIFIER").item(0).getTextContent();
		        	String database = e.getElementsByTagName("d:DATABASE").item(0).getTextContent();
		        	String proteinDescription = e.getElementsByTagName("d:PROTEIN_DESCRIPTION").item(0).getTextContent();;
		        	String peptides = e.getElementsByTagName("d:PEPTIDES").item(0).getTextContent();
		    		String unnormalizedExpression = e.getElementsByTagName("d:UNNORMALIZED_EXPRESSION").item(0).getTextContent();
		    		String normalizedExpression = e.getElementsByTagName("d:NORMALIZED_EXPRESSION").item(0).getTextContent();
		   			sql="insert into tissue_protein_proteome (TISSUE_ID,ENTRY_NAME,UNIQUE_IDENTIFIER,DATA_BASE,PROTEIN_DESCRIPTION,PEPTIDES,UNNORMALIZED_EXPRESSION,NORMALIZED_EXPRESSION) values (\""+tissueId+"\",\""+entryName+"\",\""+uniqueIdentifier+"\",\""+database+"\",\""+proteinDescription+"\",\""+peptides+"\",\""+unnormalizedExpression+"\",\""+normalizedExpression+"\")";
		   			stmt.executeUpdate(sql);
		   			System.out.println(tissueId+" :- "+entryName+" added.");
		   			
		   			/*
		   			System.out.println(uniqueIdentifier);
		   			System.out.println(database);
	    			System.out.println(proteinDescription);
		    		System.out.println(peptides);
		    		System.out.println(unnormalizedExpression);
		    		System.out.println(normalizedExpression);
		    		System.out.println("---------------------------------------------------------------------------");
					*/
		    	}
		    }
		    //System.out.println(sql);
		}
		catch(Exception e){
			errorPrintWriter.write(tissueId+"\n");
			e.printStackTrace();
		}
	}
}