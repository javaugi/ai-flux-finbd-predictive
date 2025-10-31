/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.predictive.lot47;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Lott47Analyzer {
    public static void main(String[] args) {
        Lott47Analyzer analyzer = new Lott47Analyzer();
        analyzer.analyzeLottoData();
    }

    public void analyzeLottoData() {
        List<LottoDraw> draws = CSVDataParser.getLott47Data();

        log.info("=== Michigan Lotto 47 Analysis ===");
        log.info("Total regular drawings analyzed: " + draws.size());
        log.info("Date range: 01/01/2020 to 10/25/2025\n");

        // Individual number frequency
        analyzeIndividualNumbers(draws);

        // Number pairs frequency
        analyzeNumberPairs(draws);

        // Number triplets frequency
        analyzeNumberTriplets(draws);

        // Generate likely combinations
        generateLikelyCombinations(draws);
        // Generate likely combinations
        generateLikelyCombinationsNew(draws);
    }

    private void analyzeIndividualNumbers(List<LottoDraw> draws) {
        Map<Integer, Integer> numberFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            for (int number : draw.numbers) {
                numberFrequency.put(number, numberFrequency.getOrDefault(number, 0) + 1);
            }
        }

        System.out.println("=== INDIVIDUAL NUMBER FREQUENCY ===");
        List<Map.Entry<Integer, Integer>> sortedNumbers = numberFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(20, sortedNumbers.size()); i++) {
            Map.Entry<Integer, Integer> entry = sortedNumbers.get(i);
            double percentage = (entry.getValue() * 100.0) / draws.size();
            System.out.printf("Number %2d: %3d times (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), percentage);
        }
        System.out.println();
    }

    private void analyzeNumberPairs(List<LottoDraw> draws) {
        Map<String, Integer> pairFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;

            // Generate all possible pairs
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    String pairKey = Math.min(numbers[i], numbers[j]) + "-"
                            + Math.max(numbers[i], numbers[j]);
                    pairFrequency.put(pairKey, pairFrequency.getOrDefault(pairKey, 0) + 1);
                }
            }
        }

        System.out.println("=== TOP 20 NUMBER PAIRS ===");
        List<Map.Entry<String, Integer>> sortedPairs = pairFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(20, sortedPairs.size()); i++) {
            Map.Entry<String, Integer> entry = sortedPairs.get(i);
            System.out.printf("Pair %-8s: %2d times\n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }

    private void analyzeNumberTriplets(List<LottoDraw> draws) {
        Map<String, Integer> tripletFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;

            // Generate all possible triplets
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        int[] triplet = {numbers[i], numbers[j], numbers[k]};
                        Arrays.sort(triplet);
                        String tripletKey = triplet[0] + "-" + triplet[1] + "-" + triplet[2];
                        tripletFrequency.put(tripletKey, tripletFrequency.getOrDefault(tripletKey, 0) + 1);
                    }
                }
            }
        }

        System.out.println("=== TOP 15 NUMBER TRIPLETS ===");
        List<Map.Entry<String, Integer>> sortedTriplets = tripletFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(15, sortedTriplets.size()); i++) {
            Map.Entry<String, Integer> entry = sortedTriplets.get(i);
            System.out.printf("Triplet %-12s: %2d times\n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }
    
    private List<Map.Entry<String, Integer>> getTop6NumberTriplets(List<LottoDraw> draws) {
        Map<String, Integer> tripletFrequency = new HashMap<>();
        
        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;

            // Generate all possible triplets
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        int[] triplet = {numbers[i], numbers[j], numbers[k]};
                        Arrays.sort(triplet);
                        String tripletKey = triplet[0] + "-" + triplet[1] + "-" + triplet[2];
                        tripletFrequency.put(tripletKey, tripletFrequency.getOrDefault(tripletKey, 0) + 1);
                    }
                }
            }
        }
        
        System.out.println("=== TOP 6 NUMBER TRIPLETS ===");
        List<Map.Entry<String, Integer>> sortedTriplets = tripletFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(6)
                .collect(Collectors.toList());
        
        for (int i = 0; i < sortedTriplets.size(); i++) {
            Map.Entry<String, Integer> entry = sortedTriplets.get(i);
            System.out.printf("Triplet %-12s: %2d times\n", entry.getKey(), entry.getValue());
        }
        System.out.println();

        return sortedTriplets;
    }    

    private void generateLikelyCombinations(List<LottoDraw> draws) {
        System.out.println("=== GENERATING LIKELY COMBINATIONS ===");

        // Get most frequent numbers
        List<Integer> topNumbers = getTopFrequentNumbers(draws, 15);

        // Generate combinations using different strategies
        List<int[]> combinations = new ArrayList<>();

        // Strategy 1: Balanced distribution (low, mid, high)
        combinations.add(generateBalancedCombination(topNumbers));

        // Strategy 2: Based on most frequent pairs
        combinations.add(generatePairBasedCombination(draws, topNumbers));

        // Strategy 3: Based on most frequent triplets
        combinations.add(generateTripletBasedCombination(draws, topNumbers));

        // Strategy 4: Random selection from top numbers
        combinations.add(generateRandomFromTop(topNumbers));

        // Strategy 5: Even distribution
        combinations.add(generateEvenDistributionCombination());

        System.out.println("Top 5 suggested combinations:");
        for (int i = 0; i < combinations.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, Arrays.toString(combinations.get(i)));
        }
    }

    private int[] generateTripletBasedCombinationNew(List<LottoDraw> draws, List<Integer> topNumbers) {
        // Analyze triplet frequencies
        Map<String, Integer> tripletFrequency = analyzeTripletFrequencies(draws);

        // Get top triplets
        List<int[]> topTriplets = getTopTriplets(tripletFrequency, 5);

        if (topTriplets.isEmpty()) {
            return generateRandomFromTop(topNumbers);
        }

        // Strategy: Try to combine two complementary triplets
        for (int i = 0; i < topTriplets.size(); i++) {
            for (int j = i + 1; j < topTriplets.size(); j++) {
                int[] triplet1 = topTriplets.get(i);
                int[] triplet2 = topTriplets.get(j);

                Set<Integer> combined = new HashSet<>();
                for (int num : triplet1) {
                    combined.add(num);
                }
                for (int num : triplet2) {
                    combined.add(num);
                }

                // If combining gives us exactly 6 unique numbers, we found a good combination
                if (combined.size() == 6) {
                    int[] result = combined.stream().mapToInt(Integer::intValue).toArray();
                    Arrays.sort(result);
                    return result;
                }

                // If we have more than 6, try to find the best 6
                if (combined.size() > 6) {
                    int[] result = selectBestSix(combined, topNumbers);
                    Arrays.sort(result);
                    return result;
                }
            }
        }

        // Fallback: Use the best triplet and complement with top numbers
        return completeTripletToSix(topTriplets.get(0), topNumbers);
    }

    private int[] generateTripletBasedCombinationNew2(List<LottoDraw> draws, List<Integer> topNumbers) {
        // First, analyze and get the most frequent triplets
        Map<String, Integer> tripletFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;

            // Generate all possible triplets from this draw
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        int[] triplet = {numbers[i], numbers[j], numbers[k]};
                        Arrays.sort(triplet);
                        String tripletKey = triplet[0] + "-" + triplet[1] + "-" + triplet[2];
                        tripletFrequency.put(tripletKey, tripletFrequency.getOrDefault(tripletKey, 0) + 1);
                    }
                }
            }
        }

        // Get top triplets
        List<Map.Entry<String, Integer>> sortedTriplets = tripletFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10) // Get top 10 triplets
                .collect(Collectors.toList());

        if (sortedTriplets.isEmpty()) {
            return generateRandomFromTop(topNumbers);
        }

        // Strategy 1: Use the most frequent triplet and complement with other frequent numbers
        String bestTripletStr = sortedTriplets.get(0).getKey();
        String[] tripletNumbers = bestTripletStr.split("-");

        Set<Integer> combinationSet = new HashSet<>();

        // Add the triplet numbers
        for (String numStr : tripletNumbers) {
            combinationSet.add(Integer.parseInt(numStr));
        }

        // If we need more numbers (triplet has 3, we need 6 total)
        if (combinationSet.size() < 6) {
            // Add the most frequent numbers that aren't already in the combination
            for (int num : topNumbers) {
                if (combinationSet.size() >= 6) {
                    break;
                }
                if (!combinationSet.contains(num)) {
                    combinationSet.add(num);
                }
            }
        }

        // Convert to array and sort
        int[] combination = combinationSet.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(combination);

        return combination;
    }

    private int[] generateTripletBasedCombinationNew3(List<LottoDraw> draws, List<Integer> topNumbers) {
        // Get the single most frequent triplet
        Map<String, Integer> tripletFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        int[] triplet = {numbers[i], numbers[j], numbers[k]};
                        Arrays.sort(triplet);
                        String tripletKey = triplet[0] + "-" + triplet[1] + "-" + triplet[2];
                        tripletFrequency.put(tripletKey, tripletFrequency.getOrDefault(tripletKey, 0) + 1);
                    }
                }
            }
        }

        // Find the most frequent triplet
        String bestTripletStr = tripletFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestTripletStr == null) {
            return generatePairBasedCombination(draws, topNumbers);
        }

        // Parse the triplet
        String[] parts = bestTripletStr.split("-");
        Set<Integer> combination = new HashSet<>();
        for (String part : parts) {
            combination.add(Integer.parseInt(part));
        }

        // Complete with most frequent numbers
        for (int num : topNumbers) {
            if (combination.size() >= 6) {
                break;
            }
            if (!combination.contains(num)) {
                combination.add(num);
            }
        }

        int[] result = combination.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(result);
        return result;
    }

    private int[] generateQuadrupletBasedCombination(List<LottoDraw> draws, List<Integer> topNumbers) {
        // Get the single most frequent triplet
        Map<String, Integer> tripletFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;
            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        for (int l = k + 1; l < numbers.length; l++) {
                            int[] quadruplet = {numbers[i], numbers[j], numbers[k], numbers[l]};
                            Arrays.sort(quadruplet);
                            String quadrupletKey = quadruplet[0] + "-" + quadruplet[1] + "-" + quadruplet[2] + "-" + quadruplet[3];
                            tripletFrequency.put(quadrupletKey, tripletFrequency.getOrDefault(quadrupletKey, 0) + 1);
                        }
                    }
                }
            }
        }

        // Find the most frequent triplet
        String bestTripletStr = tripletFrequency.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (bestTripletStr == null) {
            return generateTripletBasedCombinationNew2(draws, topNumbers);
        }

        // Parse the triplet
        String[] parts = bestTripletStr.split("-");
        Set<Integer> combination = new HashSet<>();
        for (String part : parts) {
            combination.add(Integer.parseInt(part));
        }

        // Complete with most frequent numbers
        for (int num : topNumbers) {
            if (combination.size() >= 6) {
                break;
            }
            if (!combination.contains(num)) {
                combination.add(num);
            }
        }

        int[] result = combination.stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(result);
        return result;
    }


    private Map<String, Integer> analyzeTripletFrequencies(List<LottoDraw> draws) {
        Map<String, Integer> tripletFrequency = new HashMap<>();

        for (LottoDraw draw : draws) {
            int[] numbers = draw.numbers;

            for (int i = 0; i < numbers.length; i++) {
                for (int j = i + 1; j < numbers.length; j++) {
                    for (int k = j + 1; k < numbers.length; k++) {
                        int[] triplet = {numbers[i], numbers[j], numbers[k]};
                        Arrays.sort(triplet);
                        String tripletKey = triplet[0] + "-" + triplet[1] + "-" + triplet[2];
                        tripletFrequency.put(tripletKey, tripletFrequency.getOrDefault(tripletKey, 0) + 1);
                    }
                }
            }
        }

        return tripletFrequency;
    }

    private List<int[]> getTopTriplets(Map<String, Integer> tripletFrequency, int count) {
        return tripletFrequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(count)
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    int[] triplet = new int[3];
                    for (int i = 0; i < 3; i++) {
                        triplet[i] = Integer.parseInt(parts[i]);
                    }
                    return triplet;
                })
                .collect(Collectors.toList());
    }

    private int[] completeTripletToSix(int[] triplet, List<Integer> topNumbers) {
        Set<Integer> combination = new HashSet<>();
        for (int num : triplet) {
            combination.add(num);
        }

        // Add the most frequent numbers that aren't in the triplet
        for (int num : topNumbers) {
            if (combination.size() >= 6) {
                break;
            }
            if (!combination.contains(num)) {
                combination.add(num);
            }
        }

        return combination.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] selectBestSix(Set<Integer> numbers, List<Integer> topNumbers) {
        // Prefer numbers that are in both the set and the top frequent numbers
        List<Integer> candidateList = new ArrayList<>(numbers);

        // Sort by frequency (appearance in topNumbers list indicates frequency)
        candidateList.sort((a, b) -> {
            int indexA = topNumbers.indexOf(a);
            int indexB = topNumbers.indexOf(b);
            // Numbers not in topNumbers go to the end
            if (indexA == -1) {
                return 1;
            }
            if (indexB == -1) {
                return -1;
            }
            return Integer.compare(indexA, indexB);
        });

        // Take the first 6 (most frequent)
        int[] result = new int[6];
        for (int i = 0; i < 6; i++) {
            result[i] = candidateList.get(i);
        }
        Arrays.sort(result);
        return result;
    }

    private void generateLikelyCombinationsNew(List<LottoDraw> draws) {
        System.out.println("\n=== NEW: GENERATING LIKELY COMBINATIONS ===");

        // Get most frequent numbers
        List<Integer> topNumbers = getTopFrequentNumbers(draws, 15);
        Collections.sort(topNumbers);
        System.out.println("=== Top 15 Numbers === \n " + topNumbers);

        Map<String, int[]> map = new HashMap<>();

        // Strategy 1: Balanced distribution (low, mid, high)
        int[] combo1 = generateBalancedCombination(topNumbers);
        map.put("Strategy 1-1: Balanced distribution (low, mid, high)   ", combo1);
        // Strategy 2: Random selection from top numbers
        int[] combo2 = generateRandomFromTop(topNumbers);
        map.put("Strategy 2-1: Random selection from top numbers        ", combo2);
        // Strategy 3: Even distribution
        int[] combo3 = generateEvenDistributionCombinationFromTops(topNumbers);
        map.put("Strategy 3-1: Even distribution from top numbers       ", combo3);

        // Strategy 4: Based on most frequent pairs
        int[] combo4 = generatePairBasedCombination(draws, topNumbers);
        int[] combo5 = generatePairBasedCombination(draws, topNumbers);
        map.put("Strategy 4-1: Based on most frequent pairs             ", combo4);
        map.put("Strategy 4-2: Based on most frequent pairs             ", combo5);

        // Strategy 5: Based on most frequent triplets
        int[] combo6 = generateTripletBasedCombinationNew(draws, topNumbers);
        int[] combo7 = generateTripletBasedCombinationNew2(draws, topNumbers);
        map.put("Strategy 5-1: Based on most frequent triplets          ", combo6);
        map.put("Strategy 5-2: Based on most frequent triplets          ", combo7);

        int[] combo8 = generateQuadrupletBasedCombination(draws, topNumbers);
        map.put("Strategy 6-1: Based on most frequent quadruplets       ", combo8);

        List<Map.Entry<String, Integer>> top6Triplets = getTop6NumberTriplets(draws);
        Map.Entry<String, Integer> e1 = top6Triplets.get(0);
        Map.Entry<String, Integer> e2 = top6Triplets.get(1);
        Map.Entry<String, Integer> e3 = top6Triplets.get(2);
        Map.Entry<String, Integer> e4 = top6Triplets.get(3);
        Map.Entry<String, Integer> e5 = top6Triplets.get(4);
        Map.Entry<String, Integer> e6 = top6Triplets.get(5);

        int[] combo9 = getComboByTwoTriplets(e1.getKey(), e2.getKey());
        int[] combo10 = getComboByTwoTriplets(e3.getKey(), e4.getKey());
        map.put("Strategy 7-1: Based on most frequent triplets          ", combo9);
        map.put("Strategy 7-2: Based on most frequent triplets          ", combo10);

        System.out.println("Top 10 suggested combinations - some of them are based on Top 20 numbers:");
        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    System.out.printf("%s  %s\n", e.getKey(), Arrays.toString(e.getValue()));
                });
    }

    private void generateLikelyCombinationsMy(List<LottoDraw> draws) {
        System.out.println("\n=== NEW: GENERATING LIKELY COMBINATIONS ===");

        // Get most frequent numbers
        List<Integer> topNumbers = getTopFrequentNumbers(draws, 15);
        Collections.sort(topNumbers);
        System.out.println("=== Top 15 Numbers === \n " + topNumbers);

        Map<String, int[]> map = new HashMap<>();

        // Strategy 1: Balanced distribution (low, mid, high)
        int[] combo1 = generateBalancedCombination(topNumbers);
        map.put("Strategy 1  : Balanced distribution (low, mid, high)   ", combo1);

        // Strategy 2: Based on most frequent pairs
        int[] combo2 = generatePairBasedCombination(draws, topNumbers);
        int[] combo3 = generatePairBasedCombination(draws, topNumbers);
        map.put("Strategy 2-1: Based on most frequent pairs             ", combo2);
        map.put("Strategy 2-2: Based on most frequent pairs             ", combo3);

        // Strategy 3: Based on most frequent triplets
        int[] combo4 = generateTripletBasedCombination(draws, topNumbers);
        int[] combo5 = generateTripletBasedCombination(draws, topNumbers);
        map.put("Strategy 3-1: Based on most frequent triplets          ", combo4);
        map.put("Strategy 3-2: Based on most frequent triplets          ", combo5);

        // Strategy 4: Random selection from top numbers
        int[] combo6 = generateRandomFromTop(topNumbers);
        map.put("Strategy 4-1: Random selection from top numbers        ", combo6);

        // Strategy 5: Even distribution
        int[] combo7 = generateEvenDistributionCombinationFromTops(topNumbers);
        map.put("Strategy 5-1: Even distribution from top numbers       ", combo7);

        List<Map.Entry<String, Integer>> top6Triplets = getTop6NumberTriplets(draws);
        Map.Entry<String, Integer> e1 = top6Triplets.get(0);
        Map.Entry<String, Integer> e2 = top6Triplets.get(1);
        Map.Entry<String, Integer> e3 = top6Triplets.get(2);
        Map.Entry<String, Integer> e4 = top6Triplets.get(3);
        Map.Entry<String, Integer> e5 = top6Triplets.get(4);
        Map.Entry<String, Integer> e6 = top6Triplets.get(5);

        int[] combo8 = getComboByTwoTriplets(e1.getKey(), e2.getKey());
        int[] combo9 = getComboByTwoTriplets(e3.getKey(), e4.getKey());
        int[] combo10 = getComboByTwoTriplets(e5.getKey(), e6.getKey());
        map.put("Strategy 6-1: Based on most frequent triplets          ", combo8);

        System.out.println("Top 10 suggested combinations - some of them are based on Top 20 numbers:");
        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
            System.out.printf("%s  %s\n", e.getKey(), Arrays.toString(e.getValue()));
        });
    }

    private int[] getComboByTwoTriplets(String key1, String key2) {
        int[] combo = new int[6];

        String[] key1Values = key1.split("-");
        String[] key2Values = key2.split("-");
        combo[0] = Integer.parseInt(key1Values[0]);
        combo[1] = Integer.parseInt(key1Values[1]);
        combo[2] = Integer.parseInt(key1Values[2]);

        combo[3] = Integer.parseInt(key2Values[0]);
        combo[4] = Integer.parseInt(key2Values[1]);
        combo[5] = Integer.parseInt(key2Values[2]);

        return combo;
    }

    private List<Integer> getTopFrequentNumbers(List<LottoDraw> draws, int count) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (LottoDraw draw : draws) {
            for (int number : draw.numbers) {
                frequency.put(number, frequency.getOrDefault(number, 0) + 1);
            }
        }

        return frequency.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private int[] generateBalancedCombination(List<Integer> topNumbers) {
        // Select 2 low (1-16), 2 mid (17-32), 2 high (33-47) numbers
        List<Integer> low = topNumbers.stream().filter(n -> n <= 16).collect(Collectors.toList());
        List<Integer> mid = topNumbers.stream().filter(n -> n > 16 && n <= 32).collect(Collectors.toList());
        List<Integer> high = topNumbers.stream().filter(n -> n > 32).collect(Collectors.toList());

        int[] combination = new int[6];
        Random random = new Random();

        // Take top 2 from each category if available, otherwise random from top numbers
        combination[0] = low.size() > 0 ? low.get(0) : topNumbers.get(random.nextInt(topNumbers.size()));
        combination[1] = low.size() > 1 ? low.get(1) : topNumbers.get(random.nextInt(topNumbers.size()));
        combination[2] = mid.size() > 0 ? mid.get(0) : topNumbers.get(random.nextInt(topNumbers.size()));
        combination[3] = mid.size() > 1 ? mid.get(1) : topNumbers.get(random.nextInt(topNumbers.size()));
        combination[4] = high.size() > 0 ? high.get(0) : topNumbers.get(random.nextInt(topNumbers.size()));
        combination[5] = high.size() > 1 ? high.get(1) : topNumbers.get(random.nextInt(topNumbers.size()));

        Arrays.sort(combination);
        return combination;
    }

    private int[] generatePairBasedCombination(List<LottoDraw> draws, List<Integer> topNumbers) {
        // This is a simplified version - you can enhance this with actual pair analysis
        Random random = new Random();
        int[] combination = new int[6];

        // Start with some of the most frequent numbers
        combination[0] = topNumbers.get(0);
        combination[1] = topNumbers.get(1);
        combination[2] = topNumbers.get(2);

        // Fill remaining with other top numbers
        for (int i = 3; i < 6; i++) {
            combination[i] = topNumbers.get(3 + random.nextInt(topNumbers.size() - 3));
        }

        Arrays.sort(combination);
        return combination;
    }

    private int[] generateTripletBasedCombination(List<LottoDraw> draws, List<Integer> topNumbers) {
        // Simplified triplet-based combination
        Random random = new Random();
        Set<Integer> used = new HashSet<>();
        int[] combination = new int[6];

        // Use top numbers ensuring variety
        for (int i = 0; i < 6; i++) {
            int num;
            do {
                num = topNumbers.get(random.nextInt(Math.min(10, topNumbers.size())));
            } while (used.contains(num));
            combination[i] = num;
            used.add(num);
        }

        Arrays.sort(combination);
        return combination;
    }

    private int[] generateRandomFromTop(List<Integer> topNumbers) {
        Random random = new Random();
        Set<Integer> used = new HashSet<>();
        int[] combination = new int[6];

        for (int i = 0; i < 6; i++) {
            int num;
            do {
                num = topNumbers.get(random.nextInt(topNumbers.size()));
            } while (used.contains(num));
            combination[i] = num;
            used.add(num);
        }

        Arrays.sort(combination);
        return combination;
    }

    private int[] generateEvenDistributionCombination() {
        // Spread numbers evenly across the range
        return new int[]{7, 15, 23, 31, 39, 47};
    }

    private int[] generateEvenDistributionCombinationFromTops(List<Integer> topNumbers) {
        // Spread numbers evenly across the range
        //{7, 15, 23, 31, 39, 47} evenly distributed
        List<Integer> low1 = topNumbers.stream().filter(n -> n <= 7).collect(Collectors.toList());
        List<Integer> low2 = topNumbers.stream().filter(n -> n > 7 && n <= 15).collect(Collectors.toList());
        List<Integer> mid1 = topNumbers.stream().filter(n -> n > 15 && n <= 23).collect(Collectors.toList());
        List<Integer> mid2 = topNumbers.stream().filter(n -> n > 23 && n <= 31).collect(Collectors.toList());
        List<Integer> high1 = topNumbers.stream().filter(n -> n > 31 && n <= 39).collect(Collectors.toList());
        List<Integer> high2 = topNumbers.stream().filter(n -> n > 39).collect(Collectors.toList());

        Random random = new Random();
        return new int[]{low1.get(random.nextInt(low1.size())),
            low2.get(random.nextInt(low2.size())),
            mid1.get(random.nextInt(mid1.size())),
            mid2.get(random.nextInt(mid2.size())),
            high1.get(random.nextInt(high1.size())),
            high2.get(random.nextInt(high2.size()))};
    }
}
