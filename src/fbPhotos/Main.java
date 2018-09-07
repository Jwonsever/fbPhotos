package fbPhotos;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//comment the above line and uncomment below line to use Chrome
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

	public static void main(String[] args) throws Exception {
		// declaration and instantiation of objects/variables
		System.setProperty("webdriver.chrome.driver", "/Users/jwonsever/Downloads/chromedriver");
		WebDriver driver = new ChromeDriver();
		try {
			String baseUrl = "https://www.facebook.com/";

			// launch Fire fox and direct it to the Base URL
			driver.get(baseUrl);

			// get the actual value of the title
			String actualTitle = driver.getTitle();

			WebElement login = driver.findElement(By.id("email"));
			WebElement pass = driver.findElement(By.id("pass"));
			WebElement submit = driver.findElement(By.id("loginbutton"));
			login.sendKeys("3018070241");
			pass.sendKeys("Up2down7");
			submit.click();

			// Load my file

			//BufferedReader in = new BufferedReader(new FileReader("/Users/jwonsever/fb/links3.txt"));
			String fails = "/Users/jwonsever/Documents/workspace/fbPhotos/results/failures.txt";
			BufferedReader in = new BufferedReader(new FileReader(fails));


			ArrayList<String> list = new ArrayList<String>();
			String sCurrentLine;

			while ((sCurrentLine = in.readLine()) != null) {
				list.add(sCurrentLine);
			}
			in.close();

			// Go through all the links I have

			for (int i = 0; i < list.size(); i++) {

				// Get my nameId
				String s = list.get(i);
				int ind = s.indexOf("fbid=");
				int endInd = s.indexOf("&", ind);
				
				if (endInd == -1) {
					endInd = s.length();
				}
				
				String id = s.substring(ind + 5, endInd);
				id.replace("fbid=", "");

				
				String sample = "https://www.facebook.com/photo.php?fbid=" + list.get(i) + "&set=t.1381012248&type=3&size=604%2C453";
				id = "retry" + list.get(i);
				driver.get(sample);

				String logoSRC = null;

				try {
		            Thread.sleep(1500);
					WebElement logo = driver.findElement(By.cssSelector(".spotlight"));
					logoSRC = logo.getAttribute("src");
				} catch (Exception e) {
					try {
						//Retry, I dont wanna deal
			            Thread.sleep(5000);
						WebElement logo = driver.findElement(By.cssSelector(".spotlight"));
						logoSRC = logo.getAttribute("src");
					} catch (Exception x) {
						e.getClass();
					}
				}

				if (logoSRC != null) {
					URL imageURL = new URL(logoSRC);
					BufferedImage saveImage = ImageIO.read(imageURL);

					ImageIO.write(saveImage, "png", new File("./results/" + id + ".png"));
				}
			}

		} finally {
			// close Fire fox
			driver.close();

		}
	}

}
