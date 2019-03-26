/////////////////////
//
//Title:           p3a Hash Table Implementation
//Files:           HashTable.java, HashTableTest.java
//Course:          LEC 002, Spring, 2019
//
//Author:          Won Woo, JU
//Email:           wju7@wisc.edu
//Lecturer's Name: Debra Deppler
/////////////////////

// Collision was resolved through array of linked nodes.
// The scheme of this hash table adaptation is buckets.
// Hashing in this adaptation uses the built in hashCode operation
// and modulus division of that value.


class Bucket<K,V> // a bucket class that stores the key, value pair
{
	K key;
	V value;
	Bucket<K,V> next;
	
	Bucket (K key, V value)
	{
		this.key = key;
		this.value = value;
		next = null;
	}
}
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {
	
	int capacity; // capacity of the hash table
	double loadFactorThreshold; 
	Bucket[] table;
	int numKeys;
	int hash; // variable storing the hashed values temporarily
		
	public HashTable() {
		capacity = 1;
		this.loadFactorThreshold = 0.5;
		Bucket[] table = null;
		numKeys = 0;
		hash = -999;
	}
	
	// overloaded constructor which accepts initial capacity and load factor threshold
	public HashTable(int initialCapacity, double loadFactorThreshold) {
		capacity = initialCapacity;
		this.loadFactorThreshold = loadFactorThreshold;
		table = new Bucket[capacity];
		numKeys = 0;
		hash = -999;
	}

	@Override
	public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {
		if (key == null) // if null key is passed, throw exception
		{
			throw new IllegalNullKeyException();
		}
		
		int hash = Math.abs(key.hashCode()) % capacity; // calculate the hash value from the key
		
		if (table[hash] == null) // if the index of table at the hashed value is null, simply insert to the hash index
		{	
			table[hash] = new Bucket(key, value);
			this.numKeys++;
		}
		else // if collision occurs
		{
			Bucket currentIndex = table[hash];
			if (currentIndex.key.equals(key)) // if key already exists, throw exception
			{
				throw new DuplicateKeyException();
			}
			while (currentIndex.next != null) // iterates through the linked nodes
			{
				if (currentIndex.key.equals(key)) // if key already exists, throw exception
				{
					throw new DuplicateKeyException();
				}
				currentIndex = currentIndex.next;
			}
			if (currentIndex.key.equals(key))
			{
				throw new DuplicateKeyException();
			}
			else
			{
				currentIndex.next = new Bucket(key, value); // inserts at the end of the linked nodes
				this.numKeys++;
			}
		}
		if (getLoadFactor() >= loadFactorThreshold) // checks whether the table needs resizing
		{
			resize();
		}
	}

	@Override
	public boolean remove(K key) throws IllegalNullKeyException {
		if(key == null) // if null key is passed, throw exception
		{
			throw new IllegalNullKeyException();
		}
		
		int hash = Math.abs(key.hashCode()) % capacity;
		Bucket previousIndex = null; // variable to point at the node prior to the one being removed
		Bucket currentIndex = table[hash];
		
		while(currentIndex != null)
		{
			if (currentIndex.key.equals(key))
			{
				if (previousIndex == null) // if there is nothing prior to the current node in the linked nodes, remove current index
				{
					currentIndex = currentIndex.next;
					numKeys--;
					return true;
				}
				
				if (previousIndex != null) // if pair to be deleted has another value in the same index, set next to skip
				{
					currentIndex.next = currentIndex.next.next;
					numKeys--;
					return true;
				}
			}
			
			previousIndex = currentIndex;
			currentIndex = currentIndex.next;
		}
		return false;
	}

	@Override
	public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
		if (key == null) // if null key is passed, throw exception
		{
			throw new IllegalNullKeyException();
		}
		
		int hash = Math.abs(key.hashCode()) % capacity;
		
		while (table[hash] != null) // if pair exists, return value
		{
			Bucket currentIndex = table[hash];
			if (currentIndex.key.equals(key))
			{
				return (V) currentIndex.value;
			}
			currentIndex = currentIndex.next; // if multiple values exist at the index, interate through
		}
		throw new KeyNotFoundException(); // if not found, throw exception
	}
	
	// returns number of keys in the table
	@Override
	public int numKeys() {
		return numKeys;
	}

	// returns the load factor threshold of the talbe
	@Override
	public double getLoadFactorThreshold() {
		return loadFactorThreshold;
	}
	
	// if necessary, resizes and rehashes the table
	private void resize() throws IllegalNullKeyException, DuplicateKeyException
	{
		capacity = (capacity*2) + 1; // new capacity of the table
		Bucket[] oldTable = table; // temporary place holder for the old table to be replaced
		table = new Bucket[capacity];
		Bucket currentIndex = null;
		this.numKeys = 0; // numKeys reset as rehashing would increase the values again
		
		for (int i=0; i<oldTable.length; i++) // iterate through the old table and insert to new
		{
			currentIndex = oldTable[i];
			while (currentIndex != null)
			{
				insert((K)currentIndex.key, (V)currentIndex.value); // rehashing
				currentIndex = currentIndex.next;
			}
		}
	}

	// returns load factor of the table
	@Override
	public double getLoadFactor() {
		double loadFactor = (double)numKeys() / (double)getCapacity();
		return loadFactor;
	}

	// returns the capacity of the table
	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getCollisionResolution() {
		return 5;
	}
		
}
