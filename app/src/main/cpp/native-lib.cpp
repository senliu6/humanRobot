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


int key_fd = 0;
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Misc_open(JNIEnv *env, jobject thiz) {
    key_fd = open("dev/hello_misc", O_RDWR);
    if(key_fd <= 0){
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/hello_misc Error fd = %d", key_fd);
    }else{
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/hello_misc Success fd = %d", key_fd);
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Misc_close(JNIEnv *env, jobject thiz) {
    if(key_fd > 0)close(key_fd);
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_Misc_read(JNIEnv *env, jobject thiz) {
    char buf[64] = {0};
    int level = read(key_fd,buf, sizeof(buf));
    return level;
}

int disinfect_fd = 0;
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLed_open(JNIEnv *env, jobject thiz) {
    disinfect_fd = open("/dev/disf_led_ctl", O_RDWR | O_NDELAY | O_NOCTTY);
    if(disinfect_fd <= 0){
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/disf_led_ctl Error fd = %d", disinfect_fd);
        __android_log_print(ANDROID_LOG_INFO, "serial", "Failed to open dri- %s", strerror(-errno));
    }else{
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/disf_led_ctl Success fd = %d", disinfect_fd);
    }
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLed_close(JNIEnv *env, jobject thiz) {
    if(disinfect_fd > 0)close(disinfect_fd);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLed_ioctl(JNIEnv *env, jobject thiz, jint num, jint en) {
    ioctl(disinfect_fd,num,en);
    return 0;
}



int status_fd = 0;
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLedStatus_open(JNIEnv *env, jobject thiz) {
    status_fd = open("/dev/disf_led_status_ctl", O_RDWR | O_NDELAY | O_NOCTTY);
    if(status_fd <= 0){
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/disf_led_status_ctl Error fd = %d", status_fd);
        __android_log_print(ANDROID_LOG_INFO, "serial", "Failed to open dri- %s", strerror(-errno));
    }else{
        __android_log_print(ANDROID_LOG_INFO, "serial", "open/dev/disf_led_status_ctl Success fd = %d", status_fd);
    }
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLedStatus_close(JNIEnv *env, jobject thiz) {
    if(status_fd > 0)close(status_fd);
    return 0;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_shciri_rosapp_peripheral_DisinfectLedStatus_ioctl(JNIEnv *env, jobject thiz, jint num,
                                                           jint en) {
    int val = ioctl(status_fd,num,en);
    return val;
}