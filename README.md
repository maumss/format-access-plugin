# Format Access Plugin

Format class accesses plugin for `NetBeans IDE 8.2+`.

## Feature

This plugin defines a Code Style for field and methods accesses.

## Install

Go to `Tools > Plugins > Downloaded Intalled > Add Plugins...` and add the `formataccessplugin.nbm` file.

##Usage

Go to Tools -> Options Menu. Choose Editor, Member Accesses and select your options to automatic opperation. You also can right click no yout text and choose `Format Accesses`. 

- Qualify all non static field accesses with 'this.'
- Qualify accesses to static fields and methods with declaring class.
- Remove 'this.' keyword from method accesses, allowing it to be overrided.
- Ignore lines marked with comment NOFORMATACCESS.
- Ignore classes related to Netbeans Swing generated forms.

## Credits
Mauricio Soares da Silva - [maumss@users.noreply.github.com](mailto:maumss@users.noreply.github.com)

## License

[GNU General Public License (GPL) v3](http://www.gnu.org/licenses/)

Copyright &copy; 2015 Mauricio Soares da Silva

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

