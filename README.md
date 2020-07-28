# Zestaw kontrolerów VR do gry Minecraft

## Efekt końcowy
 <img src="zdjecie jeden" alt="Wszystkie kontrolery">
 
### Gameplay
 [![Obejrzyj Gameplay](https://i.imgur.com/vKb2F1B.png)](https://youtu.be/vt5fpE0bzSY)

## Opis projektu
...

## Instalacja Modyfikacji
1. Pobierz plik .zip [tutaj](/modInstallationPackage.zip)
2. Zainstaluj forge-1.15.2.exe.
3. Włącz minecraft z nowo stworzoną konfiguracją "forge 1.15..." aby wygenerować pliki forge'a.
4. Wyszukaj folder %appdata% przejdź do .minecraft/mods i umieść tam serial<wersja>.jar.
5. /serial help daje do wszystkich dostępnych komend.

## Prace nad projektem
### Changelog:
[19.03.2020] Pierwszy commit 
- MODYFIKACJA:
  - init modyfikacja do minecrafta
- INNE:
  - zbieranie surowych danych z żyroskopu
 
[21.03.2020] Prace nad komunikacją
- MODYFIKACJA:
  - klasa do binarnej komunikacji PortSeryjny->Usart
  - komenda echo <wiadomość>- udaje, że dana wiadomość została odebrana na porcie seryjnym.
  - wysyłanie wiadomości jako łańcuchy binarne
 
[01.04.2020] Prace nad komunikacją
 - MODYFIKACJA:
  - interpretacja paczek kontrolujących ruch kamery
  
[13.04.2020] Łączenie Peryferiów i Optymalizacja Paczki
 - STM:
   - połączono STM z peryferiami
     - moduł HC05  (USART6)
     - konwerter USART-USB (USART2)
   - STM32F411 wysyła wiadomości z modułu USB do modułu Bluetooth, i vice-versa
 - MODYFIKACJA:
   - usunięcie pola podTypu
   - możliwość tworzenia paczek i wysyłania komendą /serial echoP <typPaczki> <typRozkazu> <arg1> <arg2> itd.
   - obsługa paska ekwipunku

### Napotkane problemy:
[13.04.2020] Złącza Arduino
- 2 z 3 Arduino mają problemy ze spawem, których w tym momencie (13.04.2020) nie można naprawić, ze względu na brak odpowiedniego sprzętu i niemożność zdobycia go z powodu kwarantanny.

## Autorzy
- Maciej Stefaniak <a href="https://github.com/madragonse">[profil Github]</a>
- Mateusz Stelmasiak <a href="https://github.com/mateusz-stelmasiak">[profil Github]</a>
- Jędrzej Wasik <a href="https://github.com/Jwasik">[profil Github]</a>

>Politechnika Poznańska 2020
