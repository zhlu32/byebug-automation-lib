package com.byebug.automation.ui.halo;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.utils.HaloUtil;
import com.byebug.automation.utils.ReportUtil;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class HaloRobot {

    private Robot robot;

    public HaloRobot() {
        try {
            robot = new Robot();
            HaloUtil.sleep(1);
        } catch (Exception e) {
            ReportUtil.log("Robot初始化异常");
        }
    }

    /**
     * 向剪切板复制文本
     *
     * @param string
     */
    public void setClipboardData(String string) {
        StringSelection stringSelection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public void clearClipboard() {
        StringSelection stringSelection = new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    /**
     * 获取剪切板文本
     *
     */
    public String getClipboardData(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        String strContent = null;
        //是否为文本类型
        if(contents.isDataFlavorSupported(DataFlavor.stringFlavor)){
            try{
                strContent = contents.getTransferData(DataFlavor.stringFlavor).toString();

            }
            catch (UnsupportedFlavorException|IOException e){
                ReportUtil.log(e.toString());
            }
        }
        if(StrUtil.isNotEmpty(strContent)){
            return strContent;
        }
        return null;
    }

    /**
     * 黏贴指定的文本
     *
     * @param string
     */
    public void pasteClipboardData(String string) {
        setClipboardData(string);
        // 模拟Ctrl+V，进行粘贴
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(1000);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
        robot.delay(1000);
    }

    /**
     * 回车
     */
    public void enter() {
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(1000);
    }

    /**
     * 点击
     */
    public void clickEnterH5() {
        robot.mouseMove(500, 500);
        click("left");
    }

    /**
     * tab
     */
    public void tab() {
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(1000);
    }

    public void press(String character) {
        //letters
        if ("a".equals(character)) {
            robot.keyPress(KeyEvent.VK_A);
        } else if ("b".equals(character)) {
            robot.keyPress(KeyEvent.VK_B);
        } else if ("c".equals(character)) {
            robot.keyPress(KeyEvent.VK_C);
        } else if ("d".equals(character)) {
            robot.keyPress(KeyEvent.VK_D);
        } else if ("e".equals(character)) {
            robot.keyPress(KeyEvent.VK_E);
        } else if ("f".equals(character)) {
            robot.keyPress(KeyEvent.VK_F);
        } else if ("g".equals(character)) {
            robot.keyPress(KeyEvent.VK_G);
        } else if ("h".equals(character)) {
            robot.keyPress(KeyEvent.VK_H);
        } else if ("i".equals(character)) {
            robot.keyPress(KeyEvent.VK_I);
        } else if ("j".equals(character)) {
            robot.keyPress(KeyEvent.VK_J);
        } else if ("k".equals(character)) {
            robot.keyPress(KeyEvent.VK_K);
        } else if ("l".equals(character)) {
            robot.keyPress(KeyEvent.VK_L);
        } else if ("m".equals(character)) {
            robot.keyPress(KeyEvent.VK_M);
        } else if ("n".equals(character)) {
            robot.keyPress(KeyEvent.VK_N);
        } else if ("o".equals(character)) {
            robot.keyPress(KeyEvent.VK_O);
        } else if ("p".equals(character)) {
            robot.keyPress(KeyEvent.VK_P);
        } else if ("q".equals(character)) {
            robot.keyPress(KeyEvent.VK_Q);
        } else if ("r".equals(character)) {
            robot.keyPress(KeyEvent.VK_R);
        } else if ("s".equals(character)) {
            robot.keyPress(KeyEvent.VK_S);
        } else if ("t".equals(character)) {
            robot.keyPress(KeyEvent.VK_T);
        } else if ("u".equals(character)) {
            robot.keyPress(KeyEvent.VK_U);
        } else if ("v".equals(character)) {
            robot.keyPress(KeyEvent.VK_V);
        } else if ("w".equals(character)) {
            robot.keyPress(KeyEvent.VK_W);
        } else if ("x".equals(character)) {
            robot.keyPress(KeyEvent.VK_X);
        } else if ("y".equals(character)) {
            robot.keyPress(KeyEvent.VK_Y);
        } else if ("z".equals(character)) {
            robot.keyPress(KeyEvent.VK_Z);
            //special
        } else if ("alt".equals(character)) {
            robot.keyPress(KeyEvent.VK_ALT);
        } else if ("tab".equals(character)) {
            robot.keyPress(KeyEvent.VK_TAB);
        } else if ("enter".equals(character)) {
            robot.keyPress(KeyEvent.VK_ENTER);
        } else if ("shift".equals(character)) {
            robot.keyPress(KeyEvent.VK_SHIFT);
        } else if ("windows".equals(character)) {
            robot.keyPress(KeyEvent.VK_WINDOWS);
        } else if ("control".equals(character)) {
            robot.keyPress(KeyEvent.VK_CONTROL);
        } else if ("open_bracket".equals(character)) {
            robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
        } else if ("escape".equals(character)) {
            robot.keyPress(KeyEvent.VK_ESCAPE);
        } else if (character.isEmpty() || character.equals(" ")) {
            robot.keyPress(KeyEvent.VK_SPACE);
        } else if ("-".equals(character)) {
            robot.keyPress(KeyEvent.VK_MINUS);
        } else if ("/".equals(character)) {
            robot.keyPress(KeyEvent.VK_SLASH);
        } else if (".".equals(character)) {
            robot.keyPress(KeyEvent.VK_PERIOD);
        }
        //numeric
        else if ("0".equals(character)) {
            robot.keyPress(KeyEvent.VK_0);
        } else if ("1".equals(character)) {
            robot.keyPress(KeyEvent.VK_1);
        } else if ("2".equals(character)) {
            robot.keyPress(KeyEvent.VK_2);
        } else if ("3".equals(character)) {
            robot.keyPress(KeyEvent.VK_3);
        } else if ("4".equals(character)) {
            robot.keyPress(KeyEvent.VK_4);
        } else if ("5".equals(character)) {
            robot.keyPress(KeyEvent.VK_5);
        } else if ("6".equals(character)) {
            robot.keyPress(KeyEvent.VK_6);
        } else if ("7".equals(character)) {
            robot.keyPress(KeyEvent.VK_7);
        } else if ("8".equals(character)) {
            robot.keyPress(KeyEvent.VK_8);
        } else if ("9".equals(character)) {
            robot.keyPress(KeyEvent.VK_9);
        } else {
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    public void release(String character) {
        //letters
        if ("a".equals(character)) {
            robot.keyRelease(KeyEvent.VK_A);
        } else if ("b".equals(character)) {
            robot.keyRelease(KeyEvent.VK_B);
        } else if ("c".equals(character)) {
            robot.keyRelease(KeyEvent.VK_C);
        } else if ("d".equals(character)) {
            robot.keyRelease(KeyEvent.VK_D);
        } else if ("e".equals(character)) {
            robot.keyRelease(KeyEvent.VK_E);
        } else if ("f".equals(character)) {
            robot.keyRelease(KeyEvent.VK_F);
        } else if ("g".equals(character)) {
            robot.keyRelease(KeyEvent.VK_G);
        } else if ("h".equals(character)) {
            robot.keyRelease(KeyEvent.VK_H);
        } else if ("i".equals(character)) {
            robot.keyRelease(KeyEvent.VK_I);
        } else if ("j".equals(character)) {
            robot.keyRelease(KeyEvent.VK_J);
        } else if ("k".equals(character)) {
            robot.keyRelease(KeyEvent.VK_K);
        } else if ("l".equals(character)) {
            robot.keyRelease(KeyEvent.VK_L);
        } else if ("m".equals(character)) {
            robot.keyRelease(KeyEvent.VK_M);
        } else if ("n".equals(character)) {
            robot.keyRelease(KeyEvent.VK_N);
        } else if ("o".equals(character)) {
            robot.keyRelease(KeyEvent.VK_O);
        } else if ("p".equals(character)) {
            robot.keyRelease(KeyEvent.VK_P);
        } else if ("q".equals(character)) {
            robot.keyRelease(KeyEvent.VK_Q);
        } else if ("r".equals(character)) {
            robot.keyRelease(KeyEvent.VK_R);
        } else if ("s".equals(character)) {
            robot.keyRelease(KeyEvent.VK_S);
        } else if ("t".equals(character)) {
            robot.keyRelease(KeyEvent.VK_T);
        } else if ("u".equals(character)) {
            robot.keyRelease(KeyEvent.VK_U);
        } else if ("v".equals(character)) {
            robot.keyRelease(KeyEvent.VK_V);
        } else if ("w".equals(character)) {
            robot.keyRelease(KeyEvent.VK_W);
        } else if ("x".equals(character)) {
            robot.keyRelease(KeyEvent.VK_X);
        } else if ("y".equals(character)) {
            robot.keyRelease(KeyEvent.VK_Y);
        } else if ("z".equals(character)) {
            robot.keyRelease(KeyEvent.VK_Z);
            //special
        } else if ("alt".equals(character)) {
            robot.keyRelease(KeyEvent.VK_ALT);
        } else if ("tab".equals(character)) {
            robot.keyRelease(KeyEvent.VK_TAB);
        } else if ("enter".equals(character)) {
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else if ("shift".equals(character)) {
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } else if ("windows".equals(character)) {
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } else if ("control".equals(character)) {
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } else if ("open_bracket".equals(character)) {
            robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
        } else if ("escape".equals(character)) {
            robot.keyRelease(KeyEvent.VK_ESCAPE);
        } else if (character.isEmpty() || character.equals(" ")) {
            robot.keyRelease(KeyEvent.VK_SPACE);
        } else if ("-".equals(character)) {
            robot.keyRelease(KeyEvent.VK_MINUS);
        } else if ("/".equals(character)) {
            robot.keyRelease(KeyEvent.VK_SLASH);
        } else if (".".equals(character)) {
            robot.keyRelease(KeyEvent.VK_PERIOD);
        }

        //numeric
        else if ("0".equals(character)) {
            robot.keyRelease(KeyEvent.VK_0);
        } else if ("1".equals(character)) {
            robot.keyRelease(KeyEvent.VK_1);
        } else if ("2".equals(character)) {
            robot.keyRelease(KeyEvent.VK_2);
        } else if ("3".equals(character)) {
            robot.keyRelease(KeyEvent.VK_3);
        } else if ("4".equals(character)) {
            robot.keyRelease(KeyEvent.VK_4);
        } else if ("5".equals(character)) {
            robot.keyRelease(KeyEvent.VK_5);
        } else if ("6".equals(character)) {
            robot.keyRelease(KeyEvent.VK_6);
        } else if ("7".equals(character)) {
            robot.keyRelease(KeyEvent.VK_7);
        } else if ("8".equals(character)) {
            robot.keyRelease(KeyEvent.VK_8);
        } else if ("9".equals(character)) {
            robot.keyRelease(KeyEvent.VK_9);
        } else {
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    public void click(String button) {
        int mouse;
        if ("left".equals(button)) {
            mouse = InputEvent.BUTTON1_MASK;
        } else if ("right".equals(button)) {
            mouse = InputEvent.BUTTON3_MASK;
        } else if ("middle".equals(button)) {
            mouse = InputEvent.BUTTON2_MASK;
        } else {
            mouse = InputEvent.BUTTON1_MASK;
        }
        robot.mousePress(mouse);
        robot.delay(1000);
        robot.mouseRelease(mouse);
        robot.delay(1000);
    }

    /***
     * 拖放鼠标
     * @param startX 起始x坐标
     * @param startY 起始y坐标
     * @param endX 结束x坐标
     * @param endY 结束y坐标
     */
    public void dragMouse(int startX,int startY,int endX,int endY){
        robot.mouseMove(startX,startY);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(500);
        //按住左键了之后还不是选中状态，需要再移动一次才真正进入选中状态
        robot.mouseMove(startX+1,startY+1);
        robot.delay(500);
        robot.mouseMove(endX,endY);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(1000);
    }

    /***
     * 模拟ctrl+c的操作
     */
    public void copyByKeyBoard(){
        clearClipboard();
        press("control");
        robot.delay(500);
        press("c");
        release("control");
        release("c");
        robot.delay(500);
    }

}
