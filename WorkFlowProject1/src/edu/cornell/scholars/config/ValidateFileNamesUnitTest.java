package edu.cornell.scholars.config;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ValidateFileNamesUnitTest {

	@Test
	public void test() {
		File folder = new File(Configuration.QUERY_RESULTSET_FOLDER +"/"+Configuration.date);
		assertTrue(folder.isDirectory());
		
		String filename [] ={
				"AllFreeTextKeywords.psv",
				"AllOrganizationsMap.psv",
				"AllMeshTerms.psv",
				"AllSubjectAreas.psv",
				"Article2KeywordSetMap.psv",
				"Article2MeSHTermsMap.psv",
				"Article2TitleAbstractMap.psv",
				"Journal2IssnEissnSubjectAreaMap.psv",
				"Person2DepartmentUnitMap.psv",
				"Awards.xml",
				"Investigators.xml",
				"AdministringDepartmentMapper.csv"
		};
		Set<String> filenames = new HashSet<String>(Arrays.asList(filename));
		
		for(File file: folder.listFiles()){
			if(file.getName().startsWith(".")) continue;
			System.out.println(file.getName());
			assertTrue(filenames.contains(file.getName()));
		}
	}

}
