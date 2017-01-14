import org.jsoup.Jsoup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RefactorFile {

	public static Boolean main(String argv[]) {

		String filepath = argv[0];
		String overwrite = argv[1];
		Boolean has_been_modified = false;

		try {
			File fXmlFile = new File(filepath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element root = doc.getDocumentElement();
			root.normalize();
			NodeList nList = doc.getElementsByTagName("theme");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Boolean modified = false;
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					
					String addon_download_name = eElement.getAttribute("id");
					System.out.println("Now processing: " + addon_download_name);
					String addon_download_link = eElement.getElementsByTagName("link").item(0).getTextContent();
					String addon_image = "";

					try {
						// Try to see if the entry has an image override tag
						// <image>
						addon_image = eElement.getElementsByTagName("image").item(0).getTextContent();
					} catch (Exception e) {
						System.out.println(addon_download_name + " has no image override tag, creating...");
					}
					
					NodeList list = eElement.getChildNodes();
					Node imageNode = null;

					for (int i = 0; i < list.getLength(); i++) {
						if (list.item(i).getNodeName().equals("image")) {
							imageNode = list.item(i);
							break;
						}
					}

					// If there is no image override tag, let's make our own!
					if (addon_image == "" || overwrite != null) {
						System.out.println("Attempting to open and modify the XML file...");
						
						String new_image = "";
						
						try {
							org.jsoup.nodes.Document doc2 = Jsoup.connect(addon_download_link).get();
							org.jsoup.nodes.Element main_head = doc2.select("div.main-content").first();
							org.jsoup.select.Elements links = main_head.getElementsByTag("img");

                            for (org.jsoup.nodes.Element link : links) {
                                String linkClass = link.className();
                                String linkAttr = link.attr("src");
                                if (linkClass.equals("cover-image")) {
                                	new_image = "http://" + linkAttr.substring(2);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            // Suppress
                        }
						
						// We have to remove the current <image> tags to prevent duplication
						if (addon_image != null && imageNode != null && !new_image.equals("")) {
								eElement.removeChild(imageNode);
						}

						if (!new_image.equals("")) {
							// New Node
							Node childnode = doc.createElement("image");
							childnode.appendChild(doc.createTextNode(new_image));
							eElement.appendChild(childnode);
	
							try {
								// Write the content into xml file
								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								DOMSource source = new DOMSource(doc);
								StreamResult result = new StreamResult(new File(filepath));
								transformer.transform(source, result);
	
								System.out.println("XML modification complete!");
								has_been_modified = true;
								modified = true;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if (!modified) {
						System.out.println(addon_download_name + " does not need to be updated.\n");
					} else {
						System.out.println(addon_download_name + " has been updated.\n");
					}
				}
			}
			return has_been_modified;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}