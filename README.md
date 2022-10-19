# JFXPlus

Adds devtools to JavaFX, also adds lots of helpful classes to streamline JavFX development. 

# DevTools

The JavaFX devtools are similar to the devtools offered in browsers, but instead made to represent a reactive JavaFX UI. The devtools display the strucutre of the UI and allow you to edit elements of the UI live. The devtools are made up of three separate tabs.

## Structure tab

![Structure tab](https://user-images.githubusercontent.com/59103153/161342067-fb2bea50-11c6-45f4-98be-9aaea8d23da3.png)

On the left of the structure tab you have a treeview representing your scene, and on the write you have three tabs you can use to manipulate the selected node.
The CSSProperties tab lets you alter certain CSS properties on the element. The Stylehsheets tab lets you view which stylesheets are applied to the node and how they effect it, and it lets you add new stylesheets to the node (these can be edited live via the Files tab). The Details tab shows you many properties on the selected node and lets you edit them live.

## Console tab

![image](https://user-images.githubusercontent.com/59103153/161342931-7f4c56e3-da66-4257-a3df-354339227979.png)

The console tab lets you execute javascript while your program is running to manipulate the scene programmatically. This can be very useful because it lets you perform powerful operations on the scene without needing to recompile and rerun the program. The console automatically has the variables `root` and `selected` set to be the root of the scene and the selected node in the Structure Tab respectively. The console also automatically imports many JavaFX related classes.

## Files tab

![image](https://user-images.githubusercontent.com/59103153/161343201-25f1e9a1-f48d-4456-9102-72e24a3bb27a.png)

The files tab displays files related to your JavaFX scene and lets you edit them live. This is especially useful for CSS since you no longer need to repeatedly recompile and rerun to get your application looking right.
