package fbPhotos;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//comment the above line and uncomment below line to use Chrome
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Main {

	public static final String baseUrl = "https://www.facebook.com/";
	public static final String photosBaseUrl = "https://www.facebook.com/photos";

	public static void main(String[] args) throws Exception {
		// declaration and instantiation of objects/variables
		System.setProperty("webdriver.chrome.driver", "./bin/chromedriver");

		Map<String, Object> prefs = new HashMap<String, Object>();             
		prefs.put("profile.default_content_setting_values.notifications", 2);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		
		WebDriver driver = new ChromeDriver(options);
		try {
			// launch Chrome and direct it to the Base URL
			driver.get(baseUrl);

			// login
			WebElement login = driver.findElement(By.id("email"));
			WebElement pass = driver.findElement(By.id("pass"));
			WebElement submit = driver.findElement(By.id("loginbutton"));
			
			Scanner myObj = new Scanner(System.in);
		    System.out.println("Enter Username / Phone Number");
		    String userName = myObj.nextLine();
		    
		    System.out.println("Enter Password");
		    String pw = myObj.nextLine();
		    
			login.sendKeys(userName);
			pass.sendKeys(pw);
			submit.click();

			// Load my file
			driver.get(photosBaseUrl);

			//BufferedReader in = new BufferedReader(new FileReader("/Users/jwonsever/fb/links3.txt"));
			//String fails = "/Users/jwonsever/Documents/workspace/fbPhotos/results/failures.txt";
			//BufferedReader in = new BufferedReader(new FileReader(fails));

            // Scroll to the bottom of the page, and let infinite scroll load everything.
            for (int i = 0; i < 200; i++) {
                Thread.sleep(500);
            	((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
			// Collect all links (with photoId)
            ArrayList<String> list = new ArrayList<String>();
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            for (WebElement link : allLinks) {
            	String href = link.getAttribute("href");
            	System.out.println("Parsing link element " + href);

            	if (href == null) {
            		continue;
            	}
            	
            	// Only save indexes with a photoId.
            	int ind = href.indexOf("fbid=");
				if (ind == -1) {
					continue;
				}
				
            	list.add(href);
            }
            
            // Make result dir.
            File f = new File("./results/");
	        f.mkdir();
	        
			// Go through all the links I have
			for (int i = 0; i < list.size(); i++) {
				String s = list.get(i);
            	System.out.println("Visiting " + s);
            	
				int ind = s.indexOf("fbid=");
				if (ind == -1) {
					continue;
				}
				
				int endInd = s.indexOf("&", ind);
				if (endInd == -1) {
					endInd = s.length();
				}
				
				String id = s.substring(ind + 5, endInd);
				id.replace("fbid=", "");				
				driver.get(s);
				String logoSRC = null;
				try {
					// Wait for the render and download.
		            Thread.sleep(1500);
					WebElement logo = driver.findElement(By.cssSelector(".spotlight"));
					logoSRC = logo.getAttribute("src");
				} catch (Exception e) {
					try {
						//Retry.  Who knows why it failed.
			            Thread.sleep(5000);
						WebElement logo = driver.findElement(By.cssSelector(".spotlight"));
						logoSRC = logo.getAttribute("src");
					} catch (Exception x) {
						e.getClass();
					}
				}

				System.out.println("Got image src: " + logoSRC);
				if (logoSRC != null) {
					URL imageURL = new URL(logoSRC);
					BufferedImage saveImage = ImageIO.read(imageURL);			        
					ImageIO.write(saveImage, "png", new File("./results/" + id + ".png"));
				}
			}

		} finally {
			driver.close();
		}
	}
}
