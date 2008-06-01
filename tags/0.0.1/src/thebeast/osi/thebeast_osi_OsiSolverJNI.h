/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class thebeast_osi_OsiSolverJNI */

#ifndef _Included_thebeast_osi_OsiSolverJNI
#define _Included_thebeast_osi_OsiSolverJNI
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    createImplementation
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_thebeast_osi_OsiSolverJNI_createImplementation
  (JNIEnv *, jclass, jint);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setHintParam
 * Signature: (IZIJ)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_OsiSolverJNI_setHintParam
  (JNIEnv *, jclass, jint, jboolean, jint, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    initialSolve
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_initialSolve
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    resolve
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_resolve
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    branchAndBound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_branchAndBound
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getNumCols
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_thebeast_osi_OsiSolverJNI_getNumCols
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setCbcLogLevel
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setCbcLogLevel
  (JNIEnv *, jclass, jint, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getNumRows
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_thebeast_osi_OsiSolverJNI_getNumRows
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getColSolution
 * Signature: (J)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_thebeast_osi_OsiSolverJNI_getColSolution
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setColLower
 * Signature: (IDJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setColLower
  (JNIEnv *, jobject, jint, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setColUpper
 * Signature: (IDJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setColUpper
  (JNIEnv *, jobject, jint, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setObjCoeff
 * Signature: (IDJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setObjCoeff
  (JNIEnv *, jobject, jint, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addCols
 * Signature: (I[I[I[D[D[D[DJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addCols
  (JNIEnv *, jobject, jint, jintArray, jintArray, jdoubleArray, jdoubleArray, jdoubleArray, jdoubleArray, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addRows
 * Signature: (I[I[I[D[D[DJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addRows
  (JNIEnv *, jobject, jint, jintArray, jintArray, jdoubleArray, jdoubleArray, jdoubleArray, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addCol
 * Signature: (I[I[DDDDJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addCol
  (JNIEnv *, jobject, jint, jintArray, jdoubleArray, jdouble, jdouble, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addRow
 * Signature: (I[I[DDDJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addRow
  (JNIEnv *, jobject, jint, jintArray, jdoubleArray, jdouble, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    reset
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_thebeast_osi_OsiSolverJNI_reset
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_delete
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getObjValue
 * Signature: (J)D
 */
JNIEXPORT jdouble JNICALL Java_thebeast_osi_OsiSolverJNI_getObjValue
  (JNIEnv *, jobject, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setObjSense
 * Signature: (DJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setObjSense
  (JNIEnv *, jobject, jdouble, jlong);

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setInteger
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setInteger
  (JNIEnv *, jobject, jint, jlong);

#ifdef __cplusplus
}
#endif
#endif
