package testPool;

import mdCoreData.ExpMass;
import mdCoreElements.Element;
import mdCoreElements.IonAdduct;
import mdGraphAssignment.RefMass;
import mdGraphConstruction.MassWrapper;
import mdGraphElements.MassDifference;
import utils.MDUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDTestPool {
    private Map<String, Element> elementMap = new TreeMap<>();
    private Map<String, IonAdduct> ionAdductMap = new TreeMap<>();
    private Map<String, ExpMass> expMassMap = new TreeMap<>();
    private Map<String, MassWrapper> massWrapperMap = new TreeMap<>();
    private Map<String, MassDifference> massDifferenceMap = new TreeMap<>();
    private Map<String, RefMass> refMassMap = new TreeMap<>();

    private static MDTestPool mdTestPool = new MDTestPool();

    private MDTestPool() {
        initialize();
    }

    public static MDTestPool getInstance() {
        return mdTestPool;
    }

    public Set<Element> getElementPool(String ... elementNames) {
        Set<Element> elements = new HashSet<>();
        for (String elementName : elementNames) {
            if (elementMap.keySet().contains(elementName)) {
                elements.add(elementMap.get(elementName));
            }
        }
        return elements;
    }

    public Set<IonAdduct> getIonAdductPool(String ... adductNames) {
        Set<IonAdduct> adducts = new HashSet<>();
        for (String adductName : adductNames) {
            if (ionAdductMap.keySet().contains(adductName)) {
                adducts.add(ionAdductMap.get(adductName));
            }
        }
        return adducts;
    }

    public List<ExpMass> getExpMassPool(String ... expMassNames) {
        List<ExpMass> expMasses = new ArrayList<>();
        for (String expMassName : expMassNames) {
            if (expMassMap.keySet().contains(expMassName)) {
                expMasses.add(expMassMap.get(expMassName));
            }
        }
        return expMasses;
    }

    public List<MassWrapper> getMassWrapperPool(String ... massWrapperNames) {
        List<MassWrapper> massWrappers = new ArrayList<>();
        for (String massWrapperName : massWrapperNames) {
            if (massWrapperMap.keySet().contains(massWrapperName)) {
                massWrappers.add(massWrapperMap.get(massWrapperName));
            }
        }
        return massWrappers;
    }

    public Set<MassDifference> getMassDifferencePool(String ... massDifferenceNames) {
        Set<MassDifference> massDifferences = new HashSet<>();
        for (String massDifferenceName : massDifferenceNames) {
            if (massDifferenceMap.keySet().contains(massDifferenceName)) {
                massDifferences.add(massDifferenceMap.get(massDifferenceName));
            }
        }
        return massDifferences;
    }

    public List<RefMass> getRefMassPool(String ... refMassNames) {
        List<RefMass> refMasses = new ArrayList<>();
        for (String refMassName : refMassNames) {
            if (refMassMap.keySet().contains(refMassName)) {
                refMasses.add(refMassMap.get(refMassName));
            }
        }
        return refMasses;
    }

    private void initialize() {
        // elements
        elementMap.put("C", new Element("C", 12.000000, 4));
        elementMap.put("H", new Element("H", 1.0078250, 1));
        elementMap.put("O", new Element("O", 15.994915, 2));
        elementMap.put("N", new Element("N", 14.003074, 3));

        // ion adducts
        ionAdductMap.put("[M+H]+", new IonAdduct("[M+H]+", IonAdduct.IonSign.POSITIVE, 1.007276));
        ionAdductMap.put("[M+Na]+", new IonAdduct("[M+Na]+", IonAdduct.IonSign.POSITIVE, 22.989221));
        ionAdductMap.put("[M-H]-", new IonAdduct("[M-H]-", IonAdduct.IonSign.NEGATIVE, -1.007276));
        ionAdductMap.put("[M+Cl]-", new IonAdduct("[M+Cl]-", IonAdduct.IonSign.NEGATIVE, 34.969401));

        // experimental masses
        List<String> formulaStrings = new ArrayList<>();
        formulaStrings.add("C6H12O6");   // id =  1 [M+H]+, id =  2 [M+Na]+, id =  3 [M-H]-, id =  4 [M+Cl]-
        formulaStrings.add("C7H14O6");   // id =  5 [M+H]+, id =  6 [M+Na]+, id =  7 [M-H]-, id =  8 [M+Cl]-
        formulaStrings.add("C7H14O7");   // id =  9 [M+H]+, id = 10 [M+Na]+, id = 11 [M-H]-, id = 12 [M+Cl]-
        formulaStrings.add("C8H16O6");   // id = 13 [M+H]+, id = 14 [M+Na]+, id = 15 [M-H]-, id = 16 [M+Cl]-
        formulaStrings.add("C7H14O8");   // id = 17 [M+H]+, id = 18 [M+Na]+, id = 19 [M-H]-, id = 20 [M+Cl]-
        formulaStrings.add("C6H12O7");   // id = 21 [M+H]+, id = 22 [M+Na]+, id = 23 [M-H]-, id = 24 [M+Cl]-
        formulaStrings.add("C6H12O8");   // id = 25 [M+H]+, id = 26 [M+Na]+, id = 27 [M-H]-, id = 28 [M+Cl]-
        formulaStrings.add("C6H11O6N");  // id = 29 [M+H]+, id = 30 [M+Na]+, id = 31 [M-H]-, id = 32 [M+Cl]-
        formulaStrings.add("C6H10O6N2"); // id = 33 [M+H]+, id = 34 [M+Na]+, id = 35 [M-H]-, id = 36 [M+Cl]-
        int idExpMass = 1;
        for (String formulaString : formulaStrings) {
            Map<Element, Integer> formula = string2Formula(formulaString);
            double formulaMass = MDUtils.getMassFromFormula(formula);
            for (IonAdduct ionAdduct : ionAdductMap.values()) {
                expMassMap.put(
                    formulaString + "_" + ionAdduct.getName(),
                    new ExpMass(idExpMass++, formulaMass + ionAdduct.getMass())
                );
            }
        }

        // mass wrappers
        Set<String> ionAdductMapKeys = ionAdductMap.keySet();
        Set<String> expMassMapKeys = expMassMap.keySet();
        for (String expMassMapKey : expMassMapKeys) {
            for (String ionAdductMapKey : ionAdductMapKeys) {
                massWrapperMap.put(
                    expMassMapKey + "_" + ionAdductMapKey,
                    new MassWrapper(
                        expMassMap.get(expMassMapKey),
                        ionAdductMap.get(ionAdductMapKey)
                    )
                );
            }
        }

        // mass differences
        formulaStrings = new ArrayList<>();
        formulaStrings.add("C");      // id = 1
        formulaStrings.add("O");      // id = 2
        formulaStrings.add("N2");     // id = 3
        formulaStrings.add("CH2");    // id = 4
        formulaStrings.add("HN-1");   // id = 5
        formulaStrings.add("H2");     // id = 6
        formulaStrings.add("C2H5OH"); // id = 7
        formulaStrings.add("NH3");    // id = 8
        int idMassDifference = 1;
        for (String formulaString : formulaStrings) {
            massDifferenceMap.put(
                formulaString,
                new MassDifference(idMassDifference, formulaString, string2Formula(formulaString))
            );
            idMassDifference++;
        }

        // reference masses
        formulaStrings = new ArrayList<>();
        formulaStrings.add("C6H12O6");
        formulaStrings.add("C6H12O7");
        for (String formulaString : formulaStrings) {
            Map<Element, Integer> formula = string2Formula(formulaString);
            for (IonAdduct ionAdduct : ionAdductMap.values()) {
                double mass = MDUtils.getMassFromFormula(formula) + ionAdduct.getMass();
                refMassMap.put(
                        formulaString + "_" + ionAdduct.getName(),
                        new RefMass(formula, mass, ionAdduct)
                );
            }
        }
    }

    private Map<Element, Integer> string2Formula(String name) {
        Map<Element, Integer> formula = new HashMap<>();
        Pattern patternElements = Pattern.compile("([A-Z][a-z]?)(-?\\d*)");
        Matcher matcherElements = patternElements.matcher(name);
        while (matcherElements.find()) {
            String elementName = matcherElements.group(1);
            int elementCount = 1;
            if (!matcherElements.group(2).isEmpty()) {
                elementCount = Integer.parseInt(matcherElements.group(2));
            }
            Element element = elementMap.get(elementName);
            if (formula.containsKey(element)) {
                formula.put(element, formula.get(element) + elementCount);
            } else {
                formula.put(element, elementCount);
            }
        }
        return formula;
    }
}
