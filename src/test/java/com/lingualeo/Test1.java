package com.lingualeo;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static org.testng.Assert.assertEquals;

/*
В этом тесте мы получаем слово из задания на сайте LinguaLeo, ищем перевод в Google и кликаем по верному слову.
Если не удалось найти верный перевод в Google, клиакем на кнопку "не знаю".
 */
public class Test1 extends Base {
    String word = null;
    String correctWord = null;
    List<WebElement> results;
    List<WebElement> resultsLeo;
    List<String> listWords;
    List<String> answers;

    public static WebDriver driver1;
    public static ChromeOptions options1;
    public static WebDriverWait wait1;

    @Test (priority = 1)
    public void loginLinguaLeo() {
        driver.get("https://lingualeo.com/");
        WebElement email = wait.until(visibilityOfElementLocated(By.name("email")));
        email.sendKeys("test555@gmail.com");
        WebElement pass = wait.until(visibilityOfElementLocated(By.name("password")));
        pass.sendKeys("test12345", Keys.ENTER);
        wait.until(ExpectedConditions.titleIs("Dashboard"));
        assertEquals( driver.getCurrentUrl(),"https://lingualeo.com/ru/dashboard");
    }

    @Test (priority = 2)
    public String chooseTheTask() {
        answers = new ArrayList<>();
        try {
            WebElement closeSellBtn = wait.until(visibilityOfElementLocated(By.cssSelector("div.ll-leokit__modal__header > div.ll-leokit__modal__close")));
            closeSellBtn.click();
        } catch (Exception ex) {
            System.out.println("Proposition wasn't shown.");
        }
        WebElement learnBtn = wait.until(elementToBeClickable(By.cssSelector("li:nth-child(3) > div > div> a[href='/ru/training']:nth-child(1)")));
        learnBtn.click();
        WebElement wordTrnslBtn = wait.until(elementToBeClickable(By.cssSelector("div.ll-leokit__kit-layer > a[href='/ru/training/wordTranslate']")));
        wordTrnslBtn.click();
        WebElement wordQ = wait.until(visibilityOfElementLocated(By.cssSelector("div.ll-WordTranslateQuestion span")));

        resultsLeo = wait.until(visibilityOfAllElementsLocatedBy(By.cssSelector("div.ll-WordTranslateAnswers button > span")));
        for (WebElement e : resultsLeo) {
            answers.add(e.getText());
        }
        return wordQ.getText();
    }

    @Test (priority = 3)
    public void print() {
        word = chooseTheTask();
    }

    @Test (dependsOnMethods = {"print"})
    public void goToGoogle() {
        options1 = new ChromeOptions();
        options1.addArguments("--disable-notifications");
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver1 = new ChromeDriver(options1);
        driver1.manage().window().maximize();
        wait1 = new WebDriverWait(driver1,5);
        driver1.get("https://translate.google.com.ua/?hl=ru&tab=wT1");

        WebElement searchField = wait1.until(visibilityOfElementLocated(By.cssSelector("textarea#source")));
        searchField.sendKeys(word);

        try {
            WebElement others = wait1.until(visibilityOfElementLocated(By.cssSelector("div.gt-cc-l-i > div > div:nth-child(3)")));
            others.click();
        } catch (Exception e){
            System.out.println(e.getClass().getSimpleName());
        } finally {
            results = wait1.until(visibilityOfAllElementsLocatedBy(By.cssSelector("div.gt-cc-l-i > div > div:nth-child(2) > table tr.gt-baf-entry td div span > span")));
        }

    }

    @Test(dependsOnMethods = {"goToGoogle"})
    public void getGoogleTranslates() {
        listWords = new ArrayList<>();
        for (WebElement el : results) {
            listWords.add(el.getText());
        }
        for (String s : answers) {
            for (String q : listWords) {
                if (s.contains(q)) {
                    correctWord = q;
                    break;
                }
            }
        }
        driver1.close();
    }

    @Test(dependsOnMethods = {"getGoogleTranslates"})
    public void enterCorrectResult() {
        System.out.println(word + " - " + correctWord);
        try {
            WebElement correctWordBtn = driver.findElement(By.xpath(String.format("//div[@class='ll-WordTranslateAnswers']//span[contains(text(), '%s')]//..", correctWord)));
            correctWordBtn.click();

            String color = driver.findElement(By.xpath(String.format("//div[@class='ll-WordTranslateAnswers']//span[contains(text(), '%s')]//..",correctWord))).getCssValue("background-color");
            assertEquals(color,"rgba(220, 245, 235, 1)");
        } catch (Exception ex) {
            System.out.println("The word has not been found on Google Translator.");
            WebElement idkBtn = driver.findElement(By.xpath("//div[@class='ll-WordTranslateAnswers']//span[contains(text(), 'не знаю')]/../.."));
            idkBtn.click();
        }
    }

}
