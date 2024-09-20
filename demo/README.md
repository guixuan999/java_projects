# No Building Tool Used

## compile
```powershell
javac -d out -cp "lib/*" top\cypherx\demo\*.java top\cypherx\uart\*.java top\cypherx\tpl\*.java
```

## make jar
```powershell
jar cfm MyApp.jar manifest.txt -C out .
```

## run
```powershell
java -jar MyApp.jar COM4
```
or run class file directly
```powershell
java -cp "out;lib/*" top.cypherx.demo.MyApp COM4
```