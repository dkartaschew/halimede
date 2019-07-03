package net.sourceforge.dkartaschew.halimede;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestListAllAlgorithms {

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}
	
	@Test
	public void listAllProvidersAlgorithms() {
		try {
			java.security.Provider p[] = Security.getProviders();
			for (int i = 0; i < p.length; i++) {
				System.out.println(p[i]);
				List<String> items = new ArrayList<>();
				for (Enumeration<Object> e = p[i].keys(); e.hasMoreElements();) {
					items.add(e.nextElement().toString());
				}
				Collections.sort(items);
				for(String s : items) {
					System.out.println("\t" + s);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
