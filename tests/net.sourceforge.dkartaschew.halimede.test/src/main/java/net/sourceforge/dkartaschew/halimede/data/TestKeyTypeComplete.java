package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

/**
 * The following test ensure we have all EC named curves that BC supports.
 */
public class TestKeyTypeComplete {

	@Test
	public void ensureCompleteSet() {
		List<String> types = Arrays.stream(KeyType.values())//
				.filter(e -> e.getParameters() != null) //
				.map(e -> e.getParameters())//
				.collect(Collectors.toList());

		List<String> names = new ArrayList<>();

		@SuppressWarnings("unchecked")
		Enumeration<String> e = ECNamedCurveTable.getNames();
		while (e.hasMoreElements()) {
			names.add(e.nextElement());
		}

		names.removeAll(types);
		assertTrue(names.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void generateKeyType() {
		gen(SECNamedCurves.getNames(), "SEC");
		gen(NISTNamedCurves.getNames(), "NIST");
		gen(X962NamedCurves.getNames(), "ANSI X9.62");
		gen(TeleTrusTNamedCurves.getNames(), "TeleTrusT");
		gen(ANSSINamedCurves.getNames(), "ANSI");
		// gen(ECGOST3410NamedCurves.getNames(), "ECGOST");
		gen(GMNamedCurves.getNames(), "GM");
	}

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void generateKeyTypeDoc() {
		System.out.println("| Curve Set      | Curves               |");
		System.out.println("|----------------|----------------------|");
		genDoc(SECNamedCurves.getNames(), "SEC");
		genDoc(NISTNamedCurves.getNames(), "NIST");
		genDoc(X962NamedCurves.getNames(), "ANSI X9.62");
		genDoc(TeleTrusTNamedCurves.getNames(), "TeleTrusT");
		genDoc(ANSSINamedCurves.getNames(), "ANSI");
		// gen(ECGOST3410NamedCurves.getNames(), "ECGOST");
		genDoc(GMNamedCurves.getNames(), "GM");
	}
	
	private void gen(Enumeration<String> names, String type) {
		List<String> namesS = new ArrayList<>();

		while (names.hasMoreElements()) {
			namesS.add(names.nextElement());
		}
		Collections.sort(namesS);

		for (String name : namesS) {
			System.out.println("/**");
			System.out.println(" * " + type + " curve " + name);
			System.out.println(" */");
			System.out.println("EC_" + name + "(\"EC " + type + " " + name + "\", \"ECDSA\", " + getCurveLength(name)
					+ ", \"" + name + "\"),");
		}
	}

	private String getCurveLength(String name) {
		return name.replaceAll("\\D+", "");
	}
	
	private void genDoc(Enumeration<String> names, String type) {
		List<String> namesS = new ArrayList<>();

		while (names.hasMoreElements()) {
			namesS.add(names.nextElement());
		}
		Collections.sort(namesS);
		System.out.print("| " + type + " | ");
		boolean start = true;
		for (String name : namesS) {
			if(!start) {
				System.out.print(", ");
			} else {
				start = false;
			}
			System.out.print(name);
		}
		System.out.println(" |");
	}
}
