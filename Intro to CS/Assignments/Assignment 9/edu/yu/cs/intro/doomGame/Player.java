package edu.yu.cs.intro.doomGame;

import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a player in the game.
 * A player whose health is <= 0 is dead.
 */
public class Player implements Comparable<Player> {

    private String name;
    private TreeSet<Weapon> weapons = new TreeSet<>(); //the weapons this player has
    private HashMap<Weapon, Integer> ammo = new HashMap<>();
    private int health;

    /**
     * @param name the player's name
     * @param health the player's starting health level
     */
    public Player(String name, int health) {
        this.name = name;
        this.health = health;
        weapons.add(Weapon.FIST);
        ammo.put(Weapon.FIST, Integer.MAX_VALUE);
    }

    public String getName(){
        return this.name;
    }

    /**
     * does this player have the given weapon?
     * @param w
     * @return
     */
    public boolean hasWeapon(Weapon w){
        return weapons.contains(w);
    }

    protected SortedSet<Weapon> getPlayersWeapons() {
        return Collections.unmodifiableSortedSet(this.weapons);
    }

    /**
     * how much ammunition does this player have for the given weapon?
     * @param w
     * @return
     */
    public int getAmmunitionRoundsForWeapon(Weapon w){
        return ammo.getOrDefault(w, 0);
    }

    /**
     * Change the ammunition amount by a positive or negative amount
     * @param weapon weapon whose ammunition count is to be changed
     * @param change amount to change ammunition count for that weapon by
     * @return the new total amount of ammunition the player has for the weapon.
     */
    public int changeAmmunitionRoundsForWeapon(Weapon weapon, int change){
        ammo.put(weapon, (ammo.getOrDefault(weapon, 0) + change));
        if (ammo.get(weapon) < 0) {
            ammo.put(weapon, 0);
        }
        return ammo.get(weapon);
    }

    /**
     * A player can have ammunition for a weapon even without having the weapon itself.
     * @param weapon weapon for which we are adding ammunition
     * @param rounds number of rounds of ammunition to add
     * @return the new total amount of ammunition the player has for the weapon
     * @throws IllegalArgumentException if rounds < 0 or weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected int addAmmunition(Weapon weapon, int rounds){
        if (weapon == null) {
            throw new IllegalArgumentException("weapon is null");
        }
        if (rounds < 0) {
            throw new IllegalArgumentException("rounds is less than 0");
        }
        if (isDead()) {
            throw new IllegalStateException("Player is dead");
        }
        
        ammo.put(weapon, (ammo.getOrDefault(weapon, 0)) + rounds);
        return ammo.get(weapon);
    }

    /**
     * When a weapon is first added to a player, the player should automatically be given 5 rounds of ammunition.
     * If the player already has the weapon before this method is called, this method has no effect at all.
     * @param weapon
     * @return true if the weapon was added, false if the player already had it
     * @throws IllegalArgumentException if weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected boolean addWeapon(Weapon weapon){
        if (weapon == null) {
           throw new IllegalArgumentException("weapon is null");
        }
        if (isDead()) {
            throw new IllegalStateException("Player is dead");
        }
        if (weapons.contains(weapon)) {
            return false;
        }
        weapons.add(weapon);
        ammo.put(weapon, (ammo.getOrDefault(weapon, 0) + 5));
        return true;
    }

    /**
     * Change the player's health level
     * @param amount a positive or negative number, to increase or decrease the player's health
     * @return the player's health level after the change
     * @throws IllegalStateException if the player is dead
     */
    public int changeHealth(int amount){
        if (isDead()) {
            throw new IllegalStateException("Player is dead");
        }
        health += amount;
        return health;
    }

    /**
     * set player's current health level to the given level
     * @param amount
     */
    protected void setHealth(int amount){
        this.health = amount;
    }

    /**
     * get the player's current health level
     * @return
     */
    public int getHealth(){
        return this.health;
    }

    /**
     * is the player dead?
     * @return
     */
    public boolean isDead(){
        return health <= 0 ? true : false;
    }

    /**
     * Compare criteria, in order:
     * Does one have a greater weapon?
     * If they have the same greatest weapon, who has more ammunition for it?
     * If they are the same on weapon and ammunition, who has more health?
     * If they are the same on greatest weapon, ammunition for it, and health, they are equal.
     * Recall that all enums have a built-in implementation of Comparable, and they compare based on ordinal()
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Player other) {
        if (this.weapons.isEmpty() && !(other.weapons.isEmpty())) {
            return -1;
        }
        if (!(this.weapons.isEmpty()) && other.weapons.isEmpty()) {
            return 1;
        }
        if (!(this.weapons.isEmpty()) && !(other.weapons.isEmpty())) {

            if (this.weapons.last().ordinal() < other.weapons.last().ordinal()) {
                return -1;
            }
            if (this.weapons.last().ordinal() > other.weapons.last().ordinal()) {
                return 1;
            }
            if (this.ammo.get(this.weapons.last()) < other.ammo.get(other.weapons.last())) {
                return -1;
            }
            if (this.ammo.get(this.weapons.last()) > other.ammo.get(other.weapons.last())) {
                return 1;
            }
        }
        if (this.health < other.health) {
            return -1;
        }
        if (this.health > other.health) {
            return 1;
        }
        return 0;
    }

    /**
     * Only equal if it is literally the same player
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return false; 
    }

    /**
     * @return the hash code of the player's name
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
