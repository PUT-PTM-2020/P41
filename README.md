# Zestaw kontrolerów VR do gry Minecraft
>PUT 2020
## Efekt końcowy



### [GamePlay](https://www.temporary-url.com/2760D)

## Postępy w pracach
[19.03.2020] Pierwszy commit 
MODYFIKACJA:
 - init modyfikacja do minecrafta
 INNE:
 - zbieranie surowych danych z żyroskopu
 
 *[21.03.2020] Prace nad komunikacją
 MODYFIKACJA:
 - klasa do binarnej komunikacji PortSeryjny->Usart
 - komenda echo <wiadomość>- udaje, że dana wiadomość została odebrana na porcie seryjnym.
 - wysyłanie wiadomości jako łańcuchy binarne
 
 *[01.04.2020] Prace nad komunikacją
  MODYFIKACJA:
  - interpretacja paczek kontrolujących ruch kamery
  
 *[13.04.2020] Łączenie Peryferiów i Optymalizacja Paczki
  STM:
  - połączono STM z peryferiami
    - moduł HC05  (USART6)
    - konwerter USART-USB (USART2)
  - STM32F411 wysyła wiadomości z modułu USB do modułu Bluetooth, i vice-versa
  MODYFIKACJA:
  - usunięcie pola podTypu
  - możliwość tworzenia paczek i wysyłania komendą /serial echoP <typPaczki> <typRozkazu> <arg1> <arg2> itd.
  - obsługa paska ekwipunku

## Napotkane problemy
*[13.04.2020] Złącza Arduino
   2 z 3 Arduino mają problemy ze spawem, których w tym momencie (13.04.2020) nie można naprawić, ze względu na brak odpowiedniego 
   sprzętu i niemożność zdobycia go z powodu kwarantanny.
## Autorzy
*Maciej Stefaniak
*Mateusz Stelmasiak
*Jędrzej Wasik