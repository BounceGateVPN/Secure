# Secure  
## Ver3.3
Fix RSA2048 method NullPointerException error.  
## ConfigReader.java
### Method
#### public ConfigReader()
```
檢查、初始化設定檔
```
#### public RSAPublicKey getPublicKey()
```
取得PublicKey
```
#### public RSAPrivateKey getPrivateKey()
```
取得PrivateKey
```
#### public Connection getSQLConnection()
```
取得SQL連線
```

## RSA.java
### Method
#### public RSA (int rsaSize)
```
rsaSize : 1024,2048,4096
```
#### public void RSAKeyGen()
```
產生Keypair，並寫到key/，格式為pem
```

## AES.java
### Method
#### public AES(int aesSize)
```
aesSize : 128,192,256
```
#### public SecretKey AESKeyGen()
```
產生AES SecretKey
```
