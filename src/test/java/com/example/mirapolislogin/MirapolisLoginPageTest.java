package com.example.mirapolislogin;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MirapolisLoginPageTest {

    MirapolisLoginPage loginPage = new MirapolisLoginPage();

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        open("https://lmslite47vr.demo.mirapolis.ru/mira");
    }

    @ParameterizedTest
    @MethodSource("provideCorrectCredentials")
    public void shouldLoginSuccessfully(String login, String password) {
        authorize(login, password);
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("mira-grid-scrollable-tile-body")));
        assertEquals("Главная страница", Selenide.title());
    }

    private void authorize(String login, String password) {
        loginPage.loginTextArea.click();
        loginPage.loginTextArea.sendKeys(login);
        loginPage.passwordTextArea.click();
        loginPage.passwordTextArea.sendKeys(password);
        loginPage.buttonLogin.click();
    }

    @Test
    public void shouldRedirectIfNotAuthorizedAfterClickingAtLogo() {
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        loginPage.logoMirapolis.click();
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("mira-default-login-page-link")));
        assertEquals("https://lmslite47vr.demo.mirapolis.ru/mira", loginPage.url);
    }

    @Test
    public void shouldOpen403PageIfAuthorizedAfterClickingAtLogo() {
        authorize("fominaelena", "8hpqLCe^");
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        webDriver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div[1]/div/div/div/div[1]/a/img")));
        loginPage.logoMirapolis.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("pagenotfound(403)!")));
        assertEquals("Доступ запрещен (403)!", Selenide.title());
    }

    @ParameterizedTest
    @MethodSource("provideWrongCredentials")
    public void shouldAlertMessageAfterWrongCredentials(String login, String password) {
        authorize(login, password);
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, 30);
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = webDriver.switchTo().alert();
        String text = alert.getText();
        String s = text.replaceAll("[.]", "");
        alert.accept();
        assertEquals("Неверные данные для авторизации", s);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    public void shouldHidePassword(int evenNumber) {
        loginPage.passwordTextArea.click();
        loginPage.passwordTextArea.sendKeys("1234");
        for (int i = 0; i < evenNumber; i++) {
            loginPage.buttonShowPassword.click();
        }
        String text = loginPage.passwordTextArea.getAttribute("type");
        assertEquals("password", text);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    public void shouldShowPassword(int oddNumber) {
        loginPage.passwordTextArea.click();
        loginPage.passwordTextArea.sendKeys("1234");
        for (int i = 0; i < oddNumber; i++) {
            loginPage.buttonShowPassword.click();
        }
        String text = loginPage.passwordTextArea.getAttribute("type");
        assertEquals("text", text);
    }

    private static Stream<Arguments> provideWrongCredentials() {
        return Stream.of(
                Arguments.of("fominaelena", "        "),
                Arguments.of("fominaelena", "8HpqLCe^"),
                Arguments.of("", "8hpqLCe^"),
                Arguments.of("fomina.elena", "8hpqLCe^")
        );
    }

    private static Stream<Arguments> provideCorrectCredentials() {
        return Stream.of(
                Arguments.of("Fominaelena", "8hpqLCe^"),
                Arguments.of("FomiNaElenA", "8hpqLCe^"),
                Arguments.of("FOMINAELENA", "8hpqLCe^")
        );
    }


    @Test
    public void shouldOpenPageUpdatePasswordAfterClickingForgotPasswordButton() {
        loginPage.buttonForgotPassword.click();
        SelenideElement element = Selenide.element(By.className("info-title"));
        assertEquals("Восстановление пароля", element.getText());
    }


    @AfterEach
    public void closeBrowser() {
        Selenide.closeWindow();
    }
}
