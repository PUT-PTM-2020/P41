  __  __ _____ _   _ ______ _____ _____            ______ _______       
 |  \/  |_   _| \ | |  ____/ ____|  __ \     /\   |  ____|__   __|      
 | \  / | | | |  \| | |__ | |    | |__) |   /  \  | |__     | |         
 | |\/| | | | | . ` |  __|| |    |  _  /   / /\ \ |  __|    | |         
 | |  | |_| |_| |\  | |___| |____| | \ \  / ____ \| |       | |         
 |_|__|_|_____|_|_\_|______\_____|_| _\_\/_/____\_\_|____ __|_|_ ______ 
 |______|______|______|______|______|______|______|______|______|______|
  / ____/ __ \| \ | |__   __|  __ \ / __ \| |    | |    |  ____|  __ \  
 | |   | |  | |  \| |  | |  | |__) | |  | | |    | |    | |__  | |__) | 
 | |   | |  | | . ` |  | |  |  _  /| |  | | |    | |    |  __| |  _  /  
 | |___| |__| | |\  |  | |  | | \ \| |__| | |____| |____| |____| | \ \  
  \_____\____/|_| \_|  |_|  |_|  \_\\____/|______|______|______|_|  \_\ 
                                                                        
PROJEKT PTM 2020

Urządzenia podłączone do STM32F411:
- HC05 module (USART6)
- USART-USB converter (USART2)

POSTĘP:

[19.03.2020] Pierwszy commit 
MODYFIKACJA:
 - init modyfikacja do minecrafta
 INNE:
 - zbieranie surowych danych z żyroskopu
 
 [21.03.2020] Prace nad komunikacją
 MODYFIKACJA:
 - klasa do binarnej komunikacji PortSeryjny->Usart
 - komenda echo <wiadomość>- udaje, że dana wiadomość została odebrana na porcie seryjnym.
 - wysyłanie wiadomości jako łańcuchy binarne
 
 [01.04.2020] Prace nad komunikacją
  MODYFIKACJA:
  - interpretacja paczek kontrolujących ruch kamery
  
  [13.04.2020] Prace nad komunikacją
  STM:
  - połączono STM z peryferiami
  - STM32F411 wysyła wiadomości z modułu USB do modułu Bluetooth, i vice-versa



                                                                        

Maciej Stefaniak
Mateusz Stelmasiak
Jędrzej Wasik
