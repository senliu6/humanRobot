#include <jni.h>
#include <string>
#include <cstdio>
#include <cstdlib>
#include <fcntl.h>
#include <cerrno>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <cstring>
#include <cstdint>
#include <termios.h>
#include <android/log.h>
#include <sys/ioctl.h>

#undef TCSAFLUSH
#define TCSAFLUSH TCSETSF

#ifndef _TERMIOS_H_
#define _TERMIOS_H_
#endif

int fd = 0;

extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Buzzer_open(JNIEnv *env, jobject thiz) {
    if ((fd = open("/dev/buzzer_ctl", O_RDWR | O_NDELAY | O_NOCTTY)) == -1) {
        __android_log_print(ANDROID_LOG_INFO, "serial", "open /dev/buzzer_ctl Error");
    } else {
        __android_log_print(ANDROID_LOG_INFO, "serial", "open /dev/buzzer_ctl Sucess fd=%d", fd);
    }
    return fd;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Buzzer_close(JNIEnv *env, jobject thiz) {
    if (fd > 0)close(fd);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Buzzer_ioctl(JNIEnv *env, jobject thiz, jint num, jint en) {
    ioctl(fd, en, num);
    return 0;
}

int led_fd = 0;
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Led_LedOpen(JNIEnv *env, jobject thiz) {
    led_fd = open("dev/leds_ctl", O_RDWR | O_NDELAY | O_NOCTTY);
    if(led_fd <= 0){
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/led_ctl Error");
    }else{
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/led_ctl Success fd = %d", led_fd);
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Led_LedClos(JNIEnv *env, jobject thiz) {
    if(led_fd > 0)close(led_fd);
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Led_LedIoctl(JNIEnv *env, jobject thiz, jint num, jint en) {
    ioctl(led_fd,num,en);
    return 0;
}