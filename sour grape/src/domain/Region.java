package domain;

public class Region {
    private String code1;
    private String code2;
    private String bun;
    private String ji;
    private boolean isSan = false;

    public Region(String code1, String code2, int bun, int ji, boolean isSan) {
        this.code1 = code1;
        this.code2 = code2;

        // 번 삽입
        if (bun > 999) {
            this.bun = String.valueOf(bun);
        } else if (bun > 99) {
            this.bun = "0" + bun;
        } else if (bun > 9) {
            this.bun = "00" + bun;
        } else if (bun > 0) {
            this.bun = "000" + bun;
        } else {
            this.bun = "0000";
        }

        // 지 삽입
        if (ji > 999) {
            this.ji = String.valueOf(ji);
        } else if (ji > 99){
            this.ji = "0" + ji;
        } else if (ji > 9) {
            this.ji = "00" + ji;
        } else if (ji > 0) {
            this.ji = "000" + ji;
        } else {
            this.ji = "0000";
        }

        this.isSan = isSan;
    }
    public Region(){}


    public void setCode1(String code1) {
        this.code1 = code1;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public void setBun(String bun) {
        this.bun = bun;
    }

    public void setJi(String ji) {
        this.ji = ji;
    }

    public void setBunIntegerInput(int bun) {
        // 번 삽입
        if (bun > 999) {
            this.bun = String.valueOf(bun);
        } else if (bun > 99) {
            this.bun = "0" + bun;
        } else if (bun > 9) {
            this.bun = "00" + bun;
        } else if (bun > 0) {
            this.bun = "000" + bun;
        } else {
            this.bun = "0000";
        }
    }

    public void setJiIntegerInput(int ji) {
        // 지 삽입
        if (ji > 999) {
            this.ji = String.valueOf(ji);
        } else if (ji > 99){
            this.ji = "0" + ji;
        } else if (ji > 9) {
            this.ji = "00" + ji;
        } else if (ji > 0) {
            this.ji = "000" + ji;
        } else {
            this.ji = "0000";
        }
    }

    public void setSan(boolean san) {
        isSan = san;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }

    public String getBun() {
        return bun;
    }

    public String getJi() {
        return ji;
    }

    public boolean isSan() {
        return isSan;
    }
}
