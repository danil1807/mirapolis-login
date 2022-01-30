package com.example.mirapolislogin;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MirapolisLoginPage {
    public String url = "https://lmslite47vr.demo.mirapolis.ru/mira";
    public SelenideElement logoMirapolis = $x("/html/body/div/div[1]/div/div/div/div[1]/a/img");
    public SelenideElement loginTextArea = $x("//*[@id=\"login_form_panel\"]/table[1]/tbody/tr[1]/td[2]/input");
    public SelenideElement passwordTextArea = $x("//*[@id=\"login_form_panel\"]/table[1]/tbody/tr[2]/td[2]/div/input");
    public SelenideElement buttonShowPassword = $x("//*[@id=\"show_password\"]");
    public SelenideElement buttonLogin = $x("//*[@id=\"button_submit_login_form\"]");
    public SelenideElement buttonForgotPassword = $x("//*[@id=\"login_form_panel\"]/table[2]/tbody/tr/td/a");


}