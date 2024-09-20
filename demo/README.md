# 不用构建工具

## compile
```powershell
javac -d out -cp "lib/jSerialComm-2.11.0.jar" top\cypherx\demo\*.java top\cypherx\uart\*.java 
```

## make jar
```powershell
jar cfm MyApp.jar manifest.txt -C out .
```

## run
```powershell
java -jar MyApp.jar
```
或者直接运行class文件
```powershell
java -cp "out;lib/jSerialComm-2.11.0.jar" top.cypherx.demo.MyApp
```