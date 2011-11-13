/**
 * Copyright (c) 2009, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 * Created on Jan 8, 2009
 * 
 */
package edu.uci.ics.jung.algorithms.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Selects items according to their probability in an arbitrary probability 
 * distribution.  The distribution is specified by a {@code Map} from
 * items (of type {@code T}) to weights of type {@code Number}, supplied
 * to the constructor; these weights are normalized internally to act as 
 * probabilities.
 * 
 * <p>This implementation selects items in O(1) time, and requires O(n) space.
 * 
 * @author Joshua O'Madadhain
 */
public class WeightedChoice<T> 
{
	private List<ItemPair> item_pairs;
	private Random random;
	
	/**
	 * The default minimum value that is treated as a valid probability 
	 * (as opposed to rounding error from floating-point operations). 
	 */
	public static final double DEFAULT_THRESHOLD = 0.00000000001;

	/**
	 * Equivalent to {@code this(item_weights, new Random(), DEFAULT_THRESHOLD)}.
	 * @param item_weights
	 */
	public WeightedChoice(Map<T, ? extends Number> item_weights)
	{
		this(item_weights, new Random(), DEFAULT_THRESHOLD);
	}

	/**
	 * Equivalent to {@code this(item_weights, new Random(), threshold)}.
	 */
	public WeightedChoice(Map<T, ? extends Number> item_weights, double threshold)
	{
		this(item_weights, new Random(), threshold);
	}
	
	/**
	 * Equivalent to {@code this(item_weights, random, DEFAULT_THRESHOLD)}.
	 */
	public WeightedChoice(Map<T, ? extends Number> item_weights, Random random)
	{
		this(item_weights, random, DEFAULT_THRESHOLD);
	}
	
	/**
	 * Creates an instance with the specified mapping from items to weights,
	 * random number generator, and threshold value.
	 * 
	 * <p>The mapping defines the weight for each item to be selected; this 
	 * will be proportional to the probability of its selection.
	 * <p>The random number generator specifies the mechanism which will be
	 * used to provide uniform integer and double values.
	 * <p>The threshold indicates default minimum value that is treated as a valid 
	 * probability (as opposed to rounding error from floating-point operations). 
	 */
	public WeightedChoice(Map<T, ? extends Number> item_weights, Random random,
			double threshold) 
	{
		if (item_weights.isEmpty())
			throw new IllegalArgumentException("Item weights must be non-empty");
		
		int item_count = item_weights.size();
		item_pairs = new ArrayList<ItemPair>(item_count);
		
		double sum = 0;
		for (Map.Entry<T, ? extends Number> entry : item_weights.entrySet())
		{
			double value = entry.getValue().doubleValue();
			if (value <= 0)
				throw new IllegalArgumentException("Weights must be > 0");
			sum += value;
		}
        double bucket_weight = 1.0 / item_weights.size();
		
		Queue<ItemPair> light_weights = new LinkedList<ItemPair>();
		Queue<ItemPair> heavy_weights = new LinkedList<ItemPair>();
		for (Map.Entry<T, ? extends Number> entry : item_weights.entrySet())
		{
			double value = entry.getValue().doubleValue() / sum;
			enqueueItem(entry.getKey(), value, bucket_weight, light_weights, heavy_weights);
		}
		
		// repeat until both queues empty
		while (!heavy_weights.isEmpty() || !light_weights.isEmpty())
		{
			ItemPair heavy_item = heavy_weights.poll();
			ItemPair light_item = light_weights.poll();
			double light_weight = 0;
			T light = null;
			T heavy = null;
			if (light_item != null)
			{
				light_weight = light_item.weight;
				light = light_item.light;
			}
			if (heavy_item != null)
			{
				heavy = heavy_item.heavy;
				// put the 'left over' weight from the heavy item--what wasn't
				// needed to make up the difference between the light weight and
				// 1/n--back in the appropriate queue
				double new_weight = heavy_item.weight - (bucket_weight - light_weight);
				if (new_weight > threshold)
					enqueueItem(heavy, new_weight, bucket_weight, light_weights, heavy_weights);
			}
			light_weight *= item_count;
			
			item_pairs.add(new ItemPair(light, heavy, light_weight));
		}
		
		this.random = random;
	}

	/**
	 * Adds key/value to the appropriate queue.  Keys with values less than
	 * the threshold get added to {@code light_weights}, all others get added
	 * to {@code heavy_weights}.
	 */
	private void enqueueItem(T key, double value, double threshold, 
			Queue<ItemPair> light_weights, Queue<ItemPair> heavy_weights)
	{
		if (value < threshold) 
			light_weights.offer(new ItemPair(key, null, value));
		else
			heavy_weights.offer(new ItemPair(null, key, value));
	}
	
	/**
	 * Sets the seed used by the internal random number generator.
	 */
	public void setRandomSeed(long seed)
	{
		this.random.setSeed(seed);
	}
	
	/**
	 * Retrieves an item with probability proportional to its weight in the
	 * {@code Map} provided in the input.
	 */
	public T nextItem()
	{
		ItemPair item_pair = item_pairs.get(random.nextInt(item_pairs.size()));
		if (random.nextDouble() < item_pair.weight)
			return item_pair.light;
		return item_pair.heavy;
	}
	
	/**
	 * Manages light object/heavy object/light conditional probability tuples.
	 */
	private class ItemPair 
	{
		T light;
		T heavy;
		double weight;
		
		private ItemPair(T light, T heavy, double weight)
		{
			this.light = light;
			this.heavy = heavy;
			this.weight = weight;
		}
		
		@Override
        public String toString()
		{
			return String.format("[L:%s, H:%s, %.3f]", light, heavy, weight);
		}
	}
}
