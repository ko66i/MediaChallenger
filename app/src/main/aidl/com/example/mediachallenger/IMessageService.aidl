// IMessageService.aidl
package com.example.mediachallenger;

// Declare any non-default types here with import statements

interface IMessageService {
       boolean playAudio();
       boolean pauseAudio();
       boolean stopAudio();
       boolean seekAudio(int position);
       int getDuration();
       int getCurrentPosition();
       boolean setVolume(float volume);
       boolean setAudioResource(String resourceName); 


}