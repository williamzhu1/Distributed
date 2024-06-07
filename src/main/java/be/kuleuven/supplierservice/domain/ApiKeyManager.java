package be.kuleuven.supplierservice.domain;

public class ApiKeyManager {


    public static String getApiKey() {
        return System.getenv("API_KEY");
    }

    public static void main(String[] args) {
        // 输出从环境变量中读取到的API密钥
        System.out.println("API Key from environment: " + getApiKey());
    }
}

