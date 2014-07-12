Manual
======

This plugin is divided into various modules. Each has a configuration section in modules.yml. Individual modules can
also be enabled / disabled there.

Each player has an "energy account" where their private energy is stored. They can view this account via the */energy*
command.

Battery
-------

- */battery <charge>* creates a battery with the given charge and removes that charge from the player's energy account.
- Clicking the helmet slot of your inventory with a battery uses up the battery and charges your energy account.

Batteries can also charge furnaces if the FurnaceChargeCommand module is enabled or discharge like DischargeCommand on
right click if DischargeCommand is enabled.

DischargeCommand
----------------

The */discharge [amount]* command discharges some or all of your energy into the environment, damaging nearby entities
and blinding them. It also charges nearby player's energy accounts. Iron armor protects against negative discharge
effects but does still allow you to charge your energy account through other player's discharges.

DisplayEnergyCommand
--------------------

*/energy [username]* displays the energy currently in your or someone else's account.

EnergyAddCommand / EnergyRemoveCommand
--------------------------------------

The */energyadd <player> <amount>* and */energyremove <player> <amount>* commands add / remove energy from another
player's account.

FurnaceChargeCommand
--------------------

Executing */charge <amount>* while looking at a furnace will use up energy in your account to fuel the furnace.

IronArmorEnergyConsumer
-----------------------

The more iron armor you wear, the more energy per second is lost in the environment because of iron's conductivity.

StrikeCommand
-------------

*/strike* will launch a lightning strike at the block you are looking at, costing you 20 energy.

TemperatureEnergyProducer
-------------------------

Any *change* (not just standing near lava!) in outside temperature generates energy for your account. This includes
light, biome temperature and hot blocks like lava and fire.

WalkEnergyProducer
------------------

Walking around charges you with small amounts of energy. This will never charge your energy above 15, however.

DeathDeductModule
-----------------

On death, half of the energy a player previously had is removed.