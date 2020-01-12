package com.lingualeo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.Assert;

public class Base {
    public static WebDriver driver;
    public static ChromeOptions options;
    public static WebDriverWait wait;

    @BeforeSuite
    public void setWebDriver() {
        options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver,15);
    }

//    @AfterSuite
//    public void closeBrowser() {
//        driver.quit();
//    }
}
