package net.sourceforge.dkartaschew.halimede;

import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestListAllAlgorithms {

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
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
