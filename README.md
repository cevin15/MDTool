# MDTool
A tool which can transfer markdown to HTML.

## How it works?
``` 
## Maven
<dependency>
    <groupId>com.youbenzi</groupId>
    <artifactId>MDTool</artifactId>
    <version>1.0.1</version>
</dependency>
```
```
MDTool.markdown2Html(new File(markdown_file_path));
```
or 
```
MDTool.markdown2Html(markdown_content);
```

## Why use it？
1. Easy to use
2. Support basic markdwon syntax
3. Support table syntax
4. No other jar dependency