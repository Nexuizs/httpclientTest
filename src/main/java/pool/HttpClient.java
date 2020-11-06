package pool;


class HCB {
    private HCB(){}

    private static class HttpClientInstance{
        private static final HCB instance = new HCB();
    }

    public static HCB getInstance(){
        return HttpClientInstance.instance;
    }
}
