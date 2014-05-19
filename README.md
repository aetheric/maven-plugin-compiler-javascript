Maven google-closure-compiler Plugin
====================================

Forked from [mdasberg/maven-closure-compiler-plugin](https://github.com/mdasberg/maven-closure-compiler-plugin), with
additions from [blizzy78/maven-closure-compiler-plugin](https://github.com/blizzy78/maven-closure-compiler-plugin).

**Usage**   
Add the following to your maven build plugins.

```XML
<plugin>
	<groupId>nz.co.aetheric.maven</groupId>
	<artifactId>plugin-closure-compiler</artifactId>
	<version>1.1.0</version>
	<configuration>
		<compilation_level>SIMPLE_OPTIMIZATIONS</compilation_level>
		<recursive>true</recursive>
		<suffix>.min.js</suffix>
		<srcDir>${project.basedir}/src/main/javascript</js_dir>
		<outDir>${project.build.outputDirectory}/script</js_output_dir>
		<version>1.0.0</version>
	</configuration>
</plugin>
```

As you can see there are 4 parameters to the operation:

1. **compilation_level** is a optional parameter which uses SIMPLE_OPTIMIZATIONS as default.
2. **recursive** is whether the file scanner should follow down directories.
3. **suffix** is what gets put at the end of the compiled filename.
2. **srcDir** is the location of the uncompiled files.
3. **outDir** is the location where the compiled files are put.
4. **version** is an optional parameter which is used to add a version number to the compiled files.    